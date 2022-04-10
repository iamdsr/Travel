package com.iamdsr.travel.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class SelectedImagesModel(
    val id: String?,
    val uri: Uri?,
    val url: String?
): Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString()
    ) {
    }

    constructor(): this("", Uri.EMPTY,"")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedImagesModel> {
        override fun createFromParcel(parcel: Parcel): SelectedImagesModel {
            return SelectedImagesModel(parcel)
        }

        override fun newArray(size: Int): Array<SelectedImagesModel?> {
            return arrayOfNulls(size)
        }
    }
}