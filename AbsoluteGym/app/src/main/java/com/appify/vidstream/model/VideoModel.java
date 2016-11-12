package com.appify.vidstream.model;

public class VideoModel {
	String videoId;
	String videoName;
	
	public VideoModel() {
	}

	public VideoModel(String videoId, String videoName) {
		super();
		this.videoId = videoId;
		this.videoName = videoName;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	
	

}
