package rx.plugins;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class RxJavaTestRunner extends RobolectricGradleTestRunner {
    public RxJavaTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        RxJavaTestPlugins.resetPlugins();
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }
}

