package ph.kana.memory.ui.fxml;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.util.Duration;

class IdleMonitor {

	private final Timeline idleTimeline;
	private final EventHandler<Event> userEventHandler;

	IdleMonitor(Duration idleTime, Runnable notifier) {
		idleTimeline = new Timeline(new KeyFrame(idleTime, e -> notifier.run()));
		idleTimeline.setCycleCount(Animation.INDEFINITE);

		userEventHandler = e -> notIdle();
	}

	void register(Scene scene, EventType<? extends  Event> eventType) {
		scene.addEventFilter(eventType, userEventHandler);
	}

	void unregister(Scene scene, EventType<? extends Event> eventType) {
		scene.removeEventFilter(eventType, userEventHandler);
	}

	void startMonitoring() {
		idleTimeline.playFromStart();
	}

	void stopMonitoring() {
		idleTimeline.stop();
	}

	private void notIdle() {
		if (Animation.Status.RUNNING == idleTimeline.getStatus()) {
			idleTimeline.playFromStart();
		}
	}
}
