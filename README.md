# rxhelper

这是一个基于RxJava2开发的脚手架工具库，你可以用它来帮助处理Android开发过程中的网络数据校验，错误转换，生命周期处理，请求重试等流程。



## 请求重试（RetryTransformers）
`RetryTransformers`里面提供了一系列的用来辅助进行重试的方法。并提供一些退避策略来指定重试的行为。

如果你想在出现任何错误都重试3次，并且每次重试前都延迟1000ms，你可以这么做：

```java
mUserDataSource.getUserInfo()
    .compose(RetryTransformers.retryAnyError(3,1000))
```

如果你只想重试指定的错误：

```java
mUserDataSource.getUserInfo()
	.compose(RetryTransformers.retryOnError(3,1000, NetworkException.class))
```

如果你想在每次重试前执行某个操作：

> 这个场景经常出现在自动登录、自动数据上传等业务逻辑里面，比如一个请求出现登录失效的错误需要重新获取Token或者需要自动登录，且自动登录完成后继续完成请求。  

```
mUserDataSource.getUserInfo()
//自动登录重试
	.compose(RetryTransformers.retryOnError(3, 1000, mUserDataSource.autoLogin(), IdentityException.class))
```

`RetryTransformers`一共提供了四类快捷实现重试的重载方法：

* `retryWhenError`搭配RetryOnError接口来判断是否应该重试

* `retryAnyError `针对任何错误都重试

* `retryOnError`针对指定错误进行重试

* `retryExceptError`针对除了指定错误之外的错误进行重试



这些重载方法都提供了一个`BackOffStrategy`的参数可以用来指定重试的退避策略，`rxhelper`内置了几种常见的退避策略

* `FixedBackOffStrategy`固定的退避策略，每次重试前delay的时间是固定的

  > new FixedBackOffStrategy(100) : 每次重试前延迟100ms
  >
  > 100,100,100、...

* `ExponentialBackOffStrategy`指数退避算法

  > new ExponentialBackOffStrategy(500, 1.5, 10 * 1000) : 第一次重试前延迟500ms,每次延迟时间为上一次的1.5倍，最大延迟时间为60*1000ms
  >
  > 500、750、1125、1687、 2531 、 3796、 5695、 8542、10000、10000、10000、...

* `IncreaseBackOffStrategy`增量退避算法

  > new IncreaseBackOffStrategy(0, 500, 3000) : 第一次重试前延迟0ms，后续每次延迟比前一次多500ms，最大延迟3000ms
  >
  > 0、500、1000、1500、2000、2500、3000、3000、3000、...



## 数据校验（ValidateTransformers）

`ValidateTransformers`主要是配合`Validator`接口对数据进行校验

通常我们希望将云端返回给我们的错误信息转化为对应的Exception,或者对云端返回的数据进行校验。那么我们可以这么做：在对应的数据模型实现`Validator`接口，并在`validate`方法中对数据进行校验和异常映射。

```java
public class CodeResp implements Validator<BaseException> {
    public static final int SUCCEED = 0;
    public static final int TOKEN_EXPIRE = 999;
    private int code;
    @Override
    public void validate() throws BaseException {
    	//将CODE为TOKEN_EXPIRE的响应转为IdentityException的异常
        if (code == TOKEN_EXPIRE) {
            throw new IdentityException(getCode(), "token expire!");
        }
    }
}
```

```java
public class MsgResp extends CodeResp {
    private String message;
    @Override
    public void validate() throws BaseException {
        super.validate();
        if (getCode() != SUCCEED) {
            if (StringUtils.isTrimEmpty(getMessage())) {
                //服务器返回的错误状态，但是没有说明错误原因
                throw new DataException(getCode(), "Error message is empty!");
            } else {
                //服务器消息类的错误转化为MsgException
                throw new MsgException(getCode(), getMessage());
            }
        }
    }
}
```

对于实现了Validator的数据可以调用`ValidateTransformers`的`validate`方法触发数据校验：

```
getUserInfo()
//数据校验
.compose(ValidateTransformers.validate())
```

也可以使用带有`DataVlidator`参数的方法对任意数据进行校验：

```
getUserInfo()
//数据校验
.compose(ValidateTransformers.validate(new DataValidator<DataResp<UserInfo>, Exception>(){
	@Override
	public void validate(DataResp<UserInfo> data) throws Exception {
		if(data.getCode() != DataResp.SUCCEED){
        	throw new MsgException(data.getCode(),data.getMessage());
        }
	}
}))
```



## 错误转换(ErrorTransformers)

`ErrorTransformers`提供了一个`convertError`的方法，里面包含一个`ErrorConverter`类型的参数，用于对错误进行转换

```java
mUserDataSource.getUserInfo()
	.compose(ErrorTransformers.convertError(BasicErrorConverter.INSTANCE))
```

```java
public class BasicErrorConverter implements ErrorConverter<BaseException> {

    public static final ErrorConverter INSTANCE = new BasicErrorConverter();

    /**
     * 将某个错误类型转换成特定的错误类型方便统一处理
     */
    @Override
    public BaseException convert(Throwable e) {
        if (e instanceof UnknownHostException
                || e instanceof ConnectException
                || e instanceof SocketTimeoutException
                || e instanceof IOException) {
            return new NetworkException(-1, "网络错误", e);
        } else if (e instanceof JSONException) {
            return new DataException(-1, "数据异常", e);
        } else if (e instanceof BaseException) {
            return (BaseException) e;
        } else {
            return new UnknowException(UnknowException.UNKNOW_CODE, e);
        }
    }
}
```

> 利用ErrorTransformers可以将错误转换为某一个统一的错误便于在UI层进行统一处理，也可以将错误转换后配合`RetryTransformers`进行重试



## 生命周期处理（LifecycleTransformer）

`LifecycleTransformer`可以将我们数据的处理流程和UI生命周期绑定起来，当UI销毁时同时释放资源和取消订阅。

`LifecycleTransformer`提供了一个`bindUntilEvent`方法，它接收一个发射事件的Observable和一个指定的事件，当这个Observable发射这个指定事件后，任务会自动取消订阅。

```
mUserDataSource.getUserInfo()
//生命周期绑定，当出现LifecycleEvent.ON_DESTROY事件时终止订阅
.compose(LifecycleTransformer.bindUntilEvent(mUserView.getLifecycleObservable(), LifecycleEvent.ON_DESTROY))
```



## 组合使用

```
new FakeApiService()
                .getUserInfo(token)
                //数据校验
                .compose(ValidateTransformers.validate())
                //数据转换
                .compose(DataTransformers.convertToData())
                //数据校验
                .compose(ValidateTransformers.validate())
                //错误转换
                .compose(ErrorTransformers.convertError(BasicErrorConverter.INSTANCE))
                //自动登录重试
                .compose(RetryTransformers.retryOnError(1, 1000, mUserDataSource.autoLogin().toObservable(), IdentityException.class))
                //其它错误重试
                .compose(RetryTransformers.retryExceptError(5, 1000, IdentityException.class))
                //绑定生命周期
            .compose(LifecycleTransformer.bindUntilEvent(mUserView.getLifecycleObservable(), LifecycleEvent.ON_DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<UserInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUserView.startGetUserInfo();
                    }

                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        mUserView.endGetUserInfo();
                        mUserView.onUserInfo(userInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mUserView.endGetUserInfo();
                        mUserView.onGetUserInfoFailed(e);
                    }
                });
```

