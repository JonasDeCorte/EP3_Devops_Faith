package com.example.ep3_devops_faith.domain

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Post(
    var Id: Long = 0L,
    var Text: String = "",
    var Picture: Bitmap? = null,
    var Link: String = "",
    var UserId: String = "",
    var UserEmail: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readParcelable(Bitmap::class.java.classLoader),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(Id)
        parcel.writeString(Text)
        parcel.writeParcelable(Picture, flags)
        parcel.writeString(Link)
        parcel.writeString(UserId)
        parcel.writeString(UserEmail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}

data class Comment(
    var Id: Long = 0L,
    var Message: String = "",
    var UserId: String = "",
    var PostId: Long = 0L,
    var UserEmail: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(Id)
        parcel.writeString(Message)
        parcel.writeString(UserId)
        parcel.writeLong(PostId)
        parcel.writeString(UserEmail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}
