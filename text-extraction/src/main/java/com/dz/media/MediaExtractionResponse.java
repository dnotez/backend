package com.dz.media;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import de.l3s.boilerpipe.document.Image;
import de.l3s.boilerpipe.document.Video;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * @author mamad
 * @since 07/02/15.
 */
public class MediaExtractionResponse {
    private final List<Image> images;
    private final List<Video> videos;

    public MediaExtractionResponse(List<Image> images, List<Video> videos) {
        this.images = firstNonNull(images, Collections.<Image>emptyList());
        this.videos = firstNonNull(videos, Collections.<Video>emptyList());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("images", images.stream().map(this::toString).collect(Collectors.toList()))
                .add("videos", videos.stream().map(this::toString).collect(Collectors.toList()))
                .toString();
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public String toString(Image image) {
        return MoreObjects.toStringHelper(this)
                .add("src", image.getSrc())
                .add("width", image.getWidth())
                .add("height", image.getHeight())
                .add("alt", image.getAlt())
                .add("area", image.getArea())
                .toString();
    }

    public String toString(Video video) {
        return MoreObjects.toStringHelper(this)
                .add("url", video.getOriginUrl())
                .add("embed", video.getEmbedUrl())
                .toString();
    }

    public Optional<Image> getMainImage() {
        return images.isEmpty() ? Optional.absent() : Optional.of(images.get(0));
    }

    public Optional<Video> getMainVideo() {
        return videos.isEmpty() ? Optional.absent() : Optional.of(videos.get(0));
    }

    public Optional<List<Image>> getExtraImages() {
        return images.size() > 1 ? Optional.of(images.subList(1, images.size())) : Optional.absent();
    }

}
