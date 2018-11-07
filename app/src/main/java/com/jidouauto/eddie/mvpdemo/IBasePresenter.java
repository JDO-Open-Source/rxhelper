package com.jidouauto.eddie.mvpdemo;

public interface IBasePresenter {
    //在View启动时调用，触发Presenter开始工作，一般在OnCreate或OnResume调用
    void subscribe();
    //View销毁前或将处于不活动状态时调用，释放相关资源，避免内存泄漏，一般在OnDestroy中调用
    void unsubscribe();
}
