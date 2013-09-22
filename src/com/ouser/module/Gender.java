package com.ouser.module;

/**
 * 
 * @author hanlixin
 * @remark 表示了Ouser模型中gender数值和枚举的对应关系
 */
public enum Gender {
	None(0),
	Male(1),
	Female(2);
	
	private int value = 0;
	private Gender(int value) {
		this.value = value;
	}
	public int getValue() {
		return this.value;
	}
	public static Gender toEnum(int value) {
		for(Gender gender : Gender.values()) {
			if(gender.getValue() == value) {
				return gender;
			}
		}
		return Gender.None;
	}
}
