package com.polycampus.android.common;

public class POJOGetAllnotice {
    String id,noticeImage,noticeName;

    public POJOGetAllnotice(String id, String noticeImage, String noticeName) {
        this.id = id;
        this.noticeImage = noticeImage;
        this.noticeName = noticeName;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;

    }

    public String getNoticeImage() {
        return noticeImage;
    }

    public String getNoticeName() {
        return noticeName;
    }
}
