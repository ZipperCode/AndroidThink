package com.think.accessibility.bean

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_dump_view_info")
data class ViewInfo(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int,
        @ColumnInfo(name = "package_name")
        val packageName: String,
        @ColumnInfo(name = "activity_name")
        val activityName: String?,
        @ColumnInfo(name = "view_id")
        val viewId: String?,
        @ColumnInfo(name = "in_screen_rect")
        val screenRect: Rect
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable<Rect>(Rect::class.java.classLoader) as Rect);

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(packageName)
        parcel.writeString(activityName)
        parcel.writeString(viewId)
        parcel.writeParcelable(screenRect, flags)
    }

    override fun describeContents()=0;
    override fun toString(): String {
        return "ViewInfo(id=$id, packageName='$packageName', activityName=$activityName, viewId=$viewId, screenRect=$screenRect)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ViewInfo

        if (packageName != other.packageName) return false
        if (activityName != other.activityName) return false
        if (viewId != other.viewId) return false
        if (screenRect != other.screenRect) return false

        return true
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + (activityName?.hashCode() ?: 0)
        result = 31 * result + (viewId?.hashCode() ?: 0)
        result = 31 * result + screenRect.hashCode()
        return result
    }


    companion object CREATOR : Parcelable.Creator<ViewInfo> {
        override fun createFromParcel(parcel: Parcel): ViewInfo {
            return ViewInfo(parcel)
        }

        override fun newArray(size: Int): Array<ViewInfo?> {
            return arrayOfNulls(size)
        }
    }


}
