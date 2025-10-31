package io.goorm.team02.core.owner.stores.events;

public class ImageCleanupEvent {
    private final String imageUrl;

    public ImageCleanupEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}