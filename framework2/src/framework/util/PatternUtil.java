/*
 * @(#)PatternUtil.java
 */
package framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 정규표현식 패턴 라이브러리
 */
public class PatternUtil {
	/**
	 * 주민등록번호 패턴
	 */
	public static final Pattern RESIDENT_REGISTRATION_NO = Pattern.compile("\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*[1-8]\\d{6}", Pattern.MULTILINE);

	public static Matcher matchResidentRegistrationNo(String residentRegistrationNo) {
		return BUSINESS_REGISTRATION_NO.matcher(residentRegistrationNo);
	}

	/**
	 * 주민등록번호 패턴
	 */
	public static final Pattern JUMIN_NO = RESIDENT_REGISTRATION_NO;

	public static Matcher matchJuminNo(String juminNo) {
		return JUMIN_NO.matcher(juminNo);
	}

	/**
	 * 법인번호 패턴
	 */
	public static final Pattern CORPORATION_REGISTRATION_NO = Pattern.compile("\\d{6}(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*\\d{7}");

	public static Matcher matchCorporationRegistrationNo(String corporationRegistrationNo) {
		return CORPORATION_REGISTRATION_NO.matcher(corporationRegistrationNo);
	}

	/**
	 * 사업자등록번호 패턴
	 */
	public static final Pattern BUSINESS_REGISTRATION_NO = Pattern.compile("[0-9]{3}(?:\\s|&nbsp;)*-(?:\\s|&nbsp;)*[0-9]{2}(?:\\s|&nbsp;)*-(?:\\s|&nbsp;)*[0-9]{5}", Pattern.MULTILINE);

	public static Matcher matchBusinessRegistrationNo(String businessRegistrationNo) {
		return BUSINESS_REGISTRATION_NO.matcher(businessRegistrationNo);
	}

	/**
	 * 신용카드번호 패턴
	 */
	public static final Pattern CREDIT_CARD_NO = Pattern.compile("(?:5[1-5]\\d{14})|(?:4\\d{12}(\\d{3})?)|(?:3[47]\\d{13})|(?:6011\\d{12})|(?:(?:30[0-5]|36\\d|38\\d)\\d{11})", Pattern.MULTILINE);

	public static Matcher matchCreditCardNo(String creditCardNo) {
		return CREDIT_CARD_NO.matcher(creditCardNo);
	}

	/**
	 * 여권번호 패턴
	 */
	public static final Pattern PASSPORT_NO = Pattern.compile("");

	public static Matcher matchPassportNo(String passportNo) {
		return PASSPORT_NO.matcher(passportNo);
	}

	/**
	 * 운전면허번호 패턴
	 */
	public static final Pattern DRIVERS_LICENSE_NO = Pattern.compile("");

	public static Matcher matchDriversLicenseNo(String driversLicenseNo) {
		return DRIVERS_LICENSE_NO.matcher(driversLicenseNo);
	}

	/**
	 * 휴대폰번호 패턴
	 */
	public static final Pattern CELLPHONE_NO = Pattern.compile("01(?:0|1|6|7|8|9)(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*(?:\\d{4}|\\d{3})(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*\\d{4}", Pattern.MULTILINE);

	public static Matcher matchCellphoneNo(String cellphoneNo) {
		return CELLPHONE_NO.matcher(cellphoneNo);
	}

	/**
	 * 일반전화번호 패턴
	 */
	public static final Pattern TELEPHONE_NO = Pattern.compile("(?:02|0[3-9]{1}[0-9]{1})(?:\\s|&nbsp;)*(?:\\)|-)?(?:\\s|&nbsp;)*(?:\\d{4}|\\d{3})(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*\\d{4}", Pattern.MULTILINE);

	public static Matcher matchTelephoneNo(String telephoneNo) {
		return TELEPHONE_NO.matcher(telephoneNo);
	}

	/**
	 * 건강보험번호 패턴
	 */
	public static final Pattern HEALTH_INSURANCE_NO = Pattern.compile("");

	public static Matcher matchHealthInsuranceNo(String healthInsuranceNo) {
		return HEALTH_INSURANCE_NO.matcher(healthInsuranceNo);
	}

	/**
	 * 계좌번호 패턴
	 */
	public static final Pattern BANK_ACCOUNT_NO = Pattern.compile("");

	public static Matcher matchBankAccountNo(String bankAccountNo) {
		return BANK_ACCOUNT_NO.matcher(bankAccountNo);
	}

	/**
	 * 이메일주소 패턴
	 */
	public static final Pattern EMAIL_ADDRESS = Pattern.compile("(?:\\w+\\.)*\\w+@(?:\\w+\\.)+[A-Za-z]+", Pattern.MULTILINE);

	public static Matcher matchEmailAddress(String emailAddress) {
		return EMAIL_ADDRESS.matcher(emailAddress);
	}

	/**
	 * 아이피주소 패턴
	 */
	public static final Pattern IP_ADDRESS = Pattern.compile("(?:(?:(?:\\d{1,2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))\\.){3}(?:(?:\\d{1,2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))", Pattern.MULTILINE);

	public static Matcher matchIPAddress(String ipAddress) {
		return IP_ADDRESS.matcher(ipAddress);
	}
}
