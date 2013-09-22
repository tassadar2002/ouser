package com.ouser.module;

import java.util.Comparator;

import android.os.Bundle;

import com.ouser.util.Const;
import com.ouser.util.Constellation;

/**
 * 藕丝
 * @author hanlixin
 *
 */
public class Ouser {

	// TODO 此处和protocol耦合了
	public enum Relation {
		None(0),
		
		/** 我关注了他 */
		Follow(1),
		
		/** 我被他关注，藕丝 */
		BeFollowed(2),
		
		/** 我拉黑了他 */
		Black(8),
		
		/** 我被他拉黑 */
		BeBlack(4),
		
		/** 自己 */
		Self(16),
		
		/** 位置 */
		Unknown(-1);
		
		private int value = 0;
		private Relation(int value) {
			this.value = value;
		}
		public int getValue() {
			return this.value;
		}
	}

	// id
	private String uid = "";
	
	/////////////静态信息/////////////
	private String nickName = "";
	private Photo portrait = new Photo();
	private PhotosWithOwner photos = new PhotosWithOwner();
	private String birthday = "";
	private int age = Const.DefaultValue.Age;
	private Gender gender = Gender.None;
	private String aboutme = "";
	private String star = "";
	private int height = Enums.self().getDefaultValue(Enums.Type.Height);
	private int body = Enums.self().getDefaultValue(Enums.Type.Body);
	private int merry = Enums.self().getDefaultValue(Enums.Type.Merry);
	private int education = Enums.self().getDefaultValue(Enums.Type.Education);
	private String school = "";
	private String company = "";
	private String jobTitle = "";
	private String hobby = "";
	private int relation = -1;
	private int level = 0;
	
	// 用户单条友约当日可发送的邀请数上限
	private int inviteCount = 0;

	/////////////动态信息/////////////
	private Location lastLocation = new Location();
	// 单位：米
	private int distance = 0;
	// 单位：分钟
	private int lastOnline = 0;
	
	/////////////数量/////////////
	private int publishAppointCount = 0;
	private int followAppointCount = 0;
	private int beFollowerCount = 0;
	private int followerCount = 0;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Photo getPortrait() {
		return portrait;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
		this.age = Constellation.getAge(birthday);
		this.star = Constellation.getStar(birthday);
	}
	public PhotosWithOwner getPhotos() {
		return photos;
	}
	public void setPhotos(PhotosWithOwner photos) {
		this.photos = photos;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getAboutme() {
		return aboutme;
	}
	public void setAboutme(String aboutme) {
		this.aboutme = aboutme;
	}
	public String getStar() {
		return star;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getBody() {
		return body;
	}
	public void setBody(int body) {
		this.body = body;
	}
	public int getMerry() {
		return merry;
	}
	public void setMerry(int merry) {
		this.merry = merry;
	}
	public int getEducation() {
		return education;
	}
	public void setEducation(int education) {
		this.education = education;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getHobby() {
		return hobby;
	}
	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
	public int getRelation() {
		return relation;
	}
	public void setRelation(int relation) {
		this.relation = relation;
	}
	public void setPortrait(Photo portrait) {
		this.portrait = portrait;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getInviteCount() {
		return inviteCount;
	}
	public void setInviteCount(int inviteCount) {
		this.inviteCount = inviteCount;
	}
	public Location getLastLocation() {
		return lastLocation;
	}
	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getLastOnline() {
		return lastOnline;
	}
	public void setLastOnline(int lastOnline) {
		this.lastOnline = lastOnline;
	}	
	public int getPublishAppointCount() {
		return publishAppointCount;
	}
	public void setPublishAppointCount(int publishAppointCount) {
		this.publishAppointCount = publishAppointCount;
	}
	public int getFollowAppointCount() {
		return followAppointCount;
	}
	public void setFollowAppointCount(int followAppointCount) {
		this.followAppointCount = followAppointCount;
	}
	public int getBeFollowerCount() {
		return beFollowerCount;
	}
	public void setBeFollowerCount(int beFollowerCount) {
		this.beFollowerCount = beFollowerCount;
	}
	public int getFollowerCount() {
		return followerCount;
	}
	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}
	
	public boolean isSame(Ouser value) {
		return value.uid.equalsIgnoreCase(uid);
	}
	
	public boolean isReleation(Relation value) {
		return (value.getValue() & relation) != 0;
	}

	// 不转换动态信息
	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putString("uid", uid);
		bundle.putString("nickName", nickName);
		bundle.putBundle("portrait", portrait.toBundle());
		bundle.putString("birthday", birthday);
		bundle.putInt("age", age);
		bundle.putInt("gender", gender.getValue());
		bundle.putString("aboutme", aboutme);
		bundle.putString("star", star);
		bundle.putInt("height", height);
		bundle.putInt("body", body);
		bundle.putInt("merry", merry);
		bundle.putInt("education", education);
		bundle.putString("school", school);
		bundle.putString("company", company);
		bundle.putString("jobTitle", jobTitle);
		bundle.putString("hobby", hobby);
		bundle.putInt("relation", relation);
		return bundle;
	}
	
	// 不转换动态信息
	public void fromBundle(Bundle bundle){
		uid = bundle.getString("uid");
		nickName = bundle.getString("nickName");
		portrait.fromBundle(bundle.getBundle("portrait"));
		birthday = bundle.getString("birthday");
		age = bundle.getInt("age");
		gender = Gender.toEnum(bundle.getInt("gender"));
		aboutme = bundle.getString("aboutme");
		star = bundle.getString("star");
		height = bundle.getInt("height");
		body = bundle.getInt("body");
		merry = bundle.getInt("merry");
		education = bundle.getInt("education");
		school = bundle.getString("school");
		company = bundle.getString("company");
		jobTitle = bundle.getString("jobTitle");
		hobby = bundle.getString("hobby");
		relation = bundle.getInt("relation");
	}
	
	public static class ComparatorByDistance implements Comparator<Ouser> {

		 public int compare(Ouser arg0, Ouser arg1) {
			 if(arg0.getDistance() < arg1.getDistance()) {
				 return -1;
			 }
			 if(arg0.getDistance() > arg1.getDistance()) {
				 return 1;
			 }
			 return 0;
		 }
	}
}
