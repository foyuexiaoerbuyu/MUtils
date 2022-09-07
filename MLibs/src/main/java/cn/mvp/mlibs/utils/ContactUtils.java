package cn.mvp.mlibs.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.List;

import cn.mvp.mlibs.log.XLogUtil;


/**
 * <uses-permission android:name="android.permission.READ_CONTACTS" />
 * <uses-permission android:name="android.permission.WRITE_CONTACTS" />
 * 通讯录工具类
 */
public class ContactUtils {
    /**
     * 批量添加通讯录联系人
     */
    public static void batchAddContact(Context context, List<Tb_contacts> list) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex;
        for (Tb_contacts contact : list) {
            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true).build());

            // 添加姓名
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                    .withYieldAllowed(true).build());
            // 添加号码
            ops.add(ContentProviderOperation
                    .newInsert(
                            android.provider.ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, contact.getNumber())
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .withValue(Phone.LABEL, "").withYieldAllowed(true).build());
        }
        // 真正添加
        ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        for (ContentProviderResult result : results) {
            XLogUtil.d("[GlobalVariables->]BatchAddContact " + result.uri.toString());
        }
    }

    /**
     * 清空系统通讯录数据
     */
    public void clearContact(Context context) {
        ContentResolver cr = context.getContentResolver();
        // 查询contacts表的所有记录
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        // 如果记录不为空
        if (cursor != null && cursor.getCount() > 0) {
            // 游标初始指向查询结果的第一条记录的上方，执行moveToNext函数会判断
            // 下一条记录是否存在，如果存在，指向下一条记录。否则，返回false。
            while (cursor.moveToNext()) {
                // 从Contacts表当中取得ContactId
//    				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                //根据姓名求id
                Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");

                Cursor cursor1 = cr.query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{name}, null);
                if (cursor1 != null && cursor1.moveToFirst()) {
                    int id = cursor1.getInt(0);
                    //根据id删除data中的相应数据
                    cr.delete(uri, "display_name=?", new String[]{name});
                    uri = Uri.parse("content://com.android.contacts/data");
                    cr.delete(uri, "raw_contact_id=?", new String[]{id + ""});
                }
                if (cursor1 != null) {
                    cursor1.close();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static class Tb_contacts {

        String name;
        String number;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }
}
//https://blog.csdn.net/fk_null/article/details/16826345?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-2.control&dist_request_id=&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-2.control
//package com.szwistar.emistar.phone;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import android.content.ContentProviderOperation;
//import android.content.ContentProviderOperation.Builder;
//import android.content.ContentProviderResult;
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.ContactsContract;
//import android.provider.ContactsContract.CommonDataKinds.Email;
//import android.provider.ContactsContract.CommonDataKinds.Event;
//import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
//import android.provider.ContactsContract.CommonDataKinds.Im;
//import android.provider.ContactsContract.CommonDataKinds.Nickname;
//import android.provider.ContactsContract.CommonDataKinds.Note;
//import android.provider.ContactsContract.CommonDataKinds.Organization;
//import android.provider.ContactsContract.CommonDataKinds.Phone;
//import android.provider.ContactsContract.CommonDataKinds.StructuredName;
//import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
//import android.provider.ContactsContract.CommonDataKinds.Website;
//import android.provider.ContactsContract.Contacts;
//import android.provider.ContactsContract.Data;
//import android.provider.ContactsContract.Groups;
//import android.provider.ContactsContract.RawContacts;
//import android.util.Log;
//
//import com.szwistar.emistar.Const;
//import com.szwistar.emistar.util.Utils;
//
///**
// * 提供管理通讯录provide
// * @author fukun
// *
// */
//public class ContactsResolver {
//
//	private ContentResolver resolver;
//
//	public ContactsResolver(ContentResolver resolver) {
//		this.resolver = resolver;
//	}
//
//	/**
//	 * 批量插入联系人
//	 * @param contacts
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public HashMap<String, Object> insertContacts(List<Map<String, Object>> contacts) {
//		HashMap<String, Object> addResult = new HashMap<String, Object>();
//		if (Utils.isEmpty(contacts)) {
//			addResult.put("result", "0");
//			addResult.put("obj", "无效插入，联系人信息不完整！");
//			return addResult;
//		}
//		//批量插入的内容集合
//		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//		ContentProviderOperation op = null;
//		int rawIndex = 0;
//		Builder builder = null;
//
//		for (Map<String, Object> contact : contacts) {
//			rawIndex = ops.size();
//
//			String displayName 			= (String) contact.get("displayName");
//			String familyName 			= (String) contact.get("familyName");
//			String middleName 			= (String) contact.get("middleName");
//			String givenName 			= (String) contact.get("givenName");
//			String prefix 				= (String) contact.get("prefix");
//			String suffix 				= (String) contact.get("suffix");
//			String phoneticName 		= (String) contact.get("phoneticName");
//			String phoneticFamilyName 	= (String) contact.get("phoneticFamilyName");
//			String phoneticMiddleName 	= (String) contact.get("phoneticMiddleName");
//			String phoneticGivenName 	= (String) contact.get("phoneticGivenName");
//			String nickName 			= (String) contact.get("nickName");
//			String birthday 			= (String) contact.get("birthday");
//			String anniversary 			= (String) contact.get("anniversary");
//			String note 				= (String) contact.get("note");
//			String company 				= (String) contact.get("company");
//			String job 					= (String) contact.get("job");
//			String department 			= (String) contact.get("department");
//
//			Map<String, Object> phones 	= (Map<String, Object>) contact.get("phones");
//			Map<String, Object> emails 	= (Map<String, Object>) contact.get("emails");
//			Map<String, Object> address = (Map<String, Object>) contact.get("addresses");
//			Map<String, Object> ims 	= (Map<String, Object>) contact.get("ims");
//			Map<String, Object> urls 	= (Map<String, Object>) contact.get("urls");
//			Map<String, Object> groups 	= (Map<String, Object>) contact.get("groups");
//
//			//如果都为空,循环下一个，联系人信息
//			if (Utils.isEmpty(displayName) && Utils.isEmpty(familyName)
//					&& Utils.isEmpty(middleName) && Utils.isEmpty(givenName)
//					&& Utils.isEmpty(prefix) && Utils.isEmpty(suffix)
//					&& Utils.isEmpty(phoneticFamilyName) && Utils.isEmpty(phoneticName)
//					&& Utils.isEmpty(phoneticGivenName) && Utils.isEmpty(phoneticMiddleName)
//					&& Utils.isEmpty(nickName) && Utils.isEmpty(birthday)
//					&& Utils.isEmpty(anniversary) && Utils.isEmpty(note)
//					&& Utils.isEmpty(company) && Utils.isEmpty(job) && Utils.isEmpty(department)
//					&& Utils.isEmpty(phones) && Utils.isEmpty(emails)
//					&& Utils.isEmpty(address) && Utils.isEmpty(ims)
//					&& Utils.isEmpty(urls)) {
//				continue;
//			}
//			//数据表 uri
//			Uri uri = Data.CONTENT_URI;
//
//			//Uri uri = RawContacts.CONTENT_URI; content://com.android.contacts/raw_contacts
//			//此处.withValue("account_name", null)一定要加，不然会抛NullPointerException
//			//withYieldAllowed(true)//为了避免这种死锁的数据库
//			op = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
//						.withValue(RawContacts.ACCOUNT_TYPE, null)
//						.withValue(RawContacts.ACCOUNT_NAME, null)
//						.withYieldAllowed(true)
//						.build();
//			ops.add(op);
//			//插入姓名
//			if (!Utils.isEmpty(displayName) || !Utils.isEmpty(familyName) || !Utils.isEmpty(middleName)
//					|| !Utils.isEmpty(givenName) || !Utils.isEmpty(prefix) || !Utils.isEmpty(suffix)
//					|| !Utils.isEmpty(phoneticName) || !Utils.isEmpty(phoneticFamilyName)
//					|| !Utils.isEmpty(phoneticMiddleName) || !Utils.isEmpty(phoneticGivenName)) {
//				Builder tempBuilder = ContentProviderOperation.newInsert(uri)
//						.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//						.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
//						.withYieldAllowed(true);
//				//插入显示名称
//				if (!Utils.isEmpty(displayName)) {
//					tempBuilder.withValue(StructuredName.DISPLAY_NAME, displayName);
//				}
//				//插入姓
//				if (!Utils.isEmpty(familyName)) {
//					tempBuilder.withValue(StructuredName.FAMILY_NAME, familyName);
//				}
//				//插入中间名
//				if (!Utils.isEmpty(middleName)) {
//					tempBuilder.withValue(StructuredName.MIDDLE_NAME, middleName);
//				}
//				//插入名
//				if (!Utils.isEmpty(givenName)) {
//					tempBuilder.withValue(StructuredName.GIVEN_NAME, givenName);
//				}
//				//插入前缀
//				if (!Utils.isEmpty(prefix)) {
//					tempBuilder.withValue(StructuredName.PREFIX, prefix);
//				}
//				//插入后缀
//				if (!Utils.isEmpty(suffix)) {
//					tempBuilder.withValue(StructuredName.SUFFIX, suffix);
//				}
//				//插入 全拼音
//				if (!Utils.isEmpty(phoneticName)) {
//					tempBuilder.withValue(StructuredName.PHONETIC_NAME, phoneticName);
//				}
//				//插入  姓拼音
//				if (!Utils.isEmpty(phoneticFamilyName)) {
//					tempBuilder.withValue(StructuredName.PHONETIC_FAMILY_NAME, phoneticFamilyName);
//				}
//				//插入中间名拼音
//				if (!Utils.isEmpty(phoneticMiddleName)) {
//					tempBuilder.withValue(StructuredName.PHONETIC_MIDDLE_NAME, phoneticMiddleName);
//				}
//				//插入名拼音
//				if (!Utils.isEmpty(phoneticGivenName)) {
//					tempBuilder.withValue(StructuredName.PHONETIC_GIVEN_NAME, phoneticGivenName);
//				}
//				ops.add(tempBuilder.build());
//			}
//			//插入昵称
//			if (!Utils.isEmpty(nickName)) {
//				op = ContentProviderOperation.newInsert(uri)
//						.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//						.withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE)
//						.withValue(Nickname.NAME, nickName)
//						.withYieldAllowed(true)
//						.build();
//				ops.add(op);
//			}
//			//插入生日
//			if (!Utils.isEmpty(birthday) || !Utils.isEmpty(anniversary)) {
//				Builder tempBuilder = ContentProviderOperation.newInsert(uri)
//						.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//						.withValue(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE)
//						.withValue(Event.TYPE, Event.TYPE_BIRTHDAY)
//						.withYieldAllowed(true);
//				//插入生日
//				if (!Utils.isEmpty(anniversary)) {
//					tempBuilder.withValue(Event.START_DATE, birthday);
//				}
//				//插入周年纪念日
//				if (!Utils.isEmpty(anniversary)) {
//					tempBuilder.withValue(Event.START_DATE, anniversary);
//				}
//				ops.add(tempBuilder.build());
//			}
//
//			//插入备注
//			if (!Utils.isEmpty(note)) {
//				op = ContentProviderOperation.newInsert(uri)
//						.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//						.withValue(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
//						.withValue(Note.NOTE, note)
//						.withYieldAllowed(true)
//						.build();
//				ops.add(op);
//			}
//			//插入组织，公司
//			if (!Utils.isEmpty(company) || !Utils.isEmpty(department) || !Utils.isEmpty(job)) {
//				Builder tempBuilder = ContentProviderOperation.newInsert(uri)
//						.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//						.withValue(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE)
//						.withValue(Organization.TYPE, Organization.TYPE_WORK)
//						.withYieldAllowed(true);
//				//插入公司
//				if (!Utils.isEmpty(company)) {
//					tempBuilder.withValue(Organization.COMPANY, company);
//				}
//				//插入部门
//				if (!Utils.isEmpty(department)) {
//					tempBuilder.withValue(Organization.DEPARTMENT, department);
//				}
//				//插入工作
//				if (!Utils.isEmpty(job)) {
//					tempBuilder.withValue(Organization.TITLE, job);
//				}
//				ops.add(tempBuilder.build());
//			}
//
//			if (!Utils.isEmpty(phones)) {
//				//插入电话号码
//				for (String key : phones.keySet()) {
//					if (!Utils.isEmpty(phones.get(key))) {
//						builder = ContentProviderOperation.newInsert(uri);
//						builder.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//							.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
//							.withValue(Phone.NUMBER, phones.get(key).toString())
//							.withValue(Phone.LABEL, "手机号")
//							.withYieldAllowed(true);
//						String temp = key.split("_")[0];
//
//						if(temp.equalsIgnoreCase("mobile")) {//如果是手机号码
//							builder.withValue(Phone.TYPE, Phone.TYPE_MOBILE);
//						} else if(temp.equalsIgnoreCase("work")) {//如果是公司电话
//							builder.withValue(Phone.TYPE, Phone.TYPE_WORK);
//						} else if(temp.equalsIgnoreCase("workMobile")) {//如果是工作手机
//							builder.withValue(Phone.TYPE, Phone.TYPE_WORK_MOBILE);
//						} else if(temp.equalsIgnoreCase("workPager")) {//如果是工作寻呼机
//							builder.withValue(Phone.TYPE, Phone.TYPE_WORK_PAGER);
//						} else if(temp.equalsIgnoreCase("faxWork")) {//如果是公司传真号码
//							builder.withValue(Phone.TYPE, Phone.TYPE_FAX_WORK);
//						} else if(temp.equalsIgnoreCase("home")) {//如果是家庭电话
//							builder.withValue(Phone.TYPE, Phone.TYPE_HOME);
//						} else if(temp.equalsIgnoreCase("faxHome")) {//如果是家庭传真
//							builder.withValue(Phone.TYPE, Phone.TYPE_FAX_HOME);
//						} else if(temp.equalsIgnoreCase("pager")) {//如果是寻呼机
//							builder.withValue(Phone.TYPE, Phone.TYPE_PAGER);
//						} else if(temp.equalsIgnoreCase("callback")) {//如果是回拨号码
//							builder.withValue(Phone.TYPE, Phone.TYPE_CALLBACK);
//						} else if(temp.equalsIgnoreCase("companyMain")) {//如果是公司总机
//							builder.withValue(Phone.TYPE, Phone.TYPE_COMPANY_MAIN);
//						} else if(temp.equalsIgnoreCase("car")) {//如果是车载电话
//							builder.withValue(Phone.TYPE, Phone.TYPE_CAR);
//						} else if(temp.equalsIgnoreCase("isdn")) {//如果是ISDN
//							builder.withValue(Phone.TYPE, Phone.TYPE_ISDN);
//						} else if(temp.equalsIgnoreCase("main")) {//如果是总机
//							builder.withValue(Phone.TYPE, Phone.TYPE_MAIN);
//						} else if(temp.equalsIgnoreCase("radio")) {//如果是无线装置
//							builder.withValue(Phone.TYPE, Phone.TYPE_RADIO);
//						} else if(temp.equalsIgnoreCase("telex")) {//如果是电报
//							builder.withValue(Phone.TYPE, Phone.TYPE_TELEX);
//						} else if(temp.equalsIgnoreCase("ttyTdd")) {//如果是TTY_TDD
//							builder.withValue(Phone.TYPE, Phone.TYPE_TTY_TDD);
//						} else if(temp.equalsIgnoreCase("assistant")) {//如果是助理
//							builder.withValue(Phone.TYPE, Phone.TYPE_ASSISTANT);
//						} else if(temp.equalsIgnoreCase("mms")) {//如果是彩信
//							builder.withValue(Phone.TYPE, Phone.TYPE_MMS);
//						} else {//其他
//							builder.withValue(Phone.TYPE, Phone.TYPE_OTHER);
//						}
//
//						ops.add(builder.build());
//					}
//				}
//			}
//			//插入电子邮件
//			if (!Utils.isEmpty(emails)) {
//				for (String key : emails.keySet()) {
//					if (!Utils.isEmpty(emails.get(key))) {
//						builder = ContentProviderOperation.newInsert(uri);
//						builder.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//							.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
//							.withValue(Email.DATA, emails.get(key).toString())
//							.withYieldAllowed(true);
//						String temp = key.split("_")[0];
//
//						if(temp.equalsIgnoreCase("home")) {//如果是家庭邮件
//							builder.withValue(Email.TYPE, Email.TYPE_HOME);
//						} else if(temp.equalsIgnoreCase("work")) {//如果是公司邮件
//							builder.withValue(Email.TYPE, Email.TYPE_WORK);
//						} else if(temp.equalsIgnoreCase("custom")) {//如果是自定义邮件地址
//							builder.withValue(Email.TYPE, Email.TYPE_CUSTOM);
//						} else if(temp.equalsIgnoreCase("mobile")) {//如果是手机邮件地址
//							builder.withValue(Email.TYPE, Email.TYPE_MOBILE);
//						} else {//其他
//							builder.withValue(Email.TYPE, Email.TYPE_OTHER);
//						}
//						ops.add(builder.build());
//					}
//				}
//			}
//			//插入地址
//			if (!Utils.isEmpty(address)) {
//				for (String key : address.keySet()) {
//					if (!Utils.isEmpty(address.get(key))) {
//						builder = ContentProviderOperation.newInsert(uri);
//						builder.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//							.withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
//							.withValue(StructuredPostal.FORMATTED_ADDRESS, address.get(key).toString())
//							.withYieldAllowed(true);
//						String temp = key.split("_")[0];
//						if(temp.equalsIgnoreCase("home")) {//如果是家庭地址
//							builder.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME);
//						} else if(temp.equalsIgnoreCase("work")) {//如果是公司地址
//							builder.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_WORK);
//						} else if(temp.equalsIgnoreCase("custom")) {//如果是自定义地址
//							builder.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_CUSTOM);
//						} else {//其他
//							builder.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_OTHER);
//						}
//						ops.add(builder.build());
//					}
//				}
//			}
//			//插入即时消息ims
//			if (!Utils.isEmpty(ims)) {
//				for (String key : ims.keySet()) {
//					if (!Utils.isEmpty(ims.get(key))) {
//						builder = ContentProviderOperation.newInsert(uri);
//						builder.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//							.withValue(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE)
//							.withValue(Im.DATA, ims.get(key).toString())
//							.withYieldAllowed(true);
//						String temp = key.split("_")[0];
//						if(temp.equalsIgnoreCase("aim")) {//如果是aim
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_AIM);
//						} else if(temp.equalsIgnoreCase("msn")) {//如果是msn
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_MSN);
//						} else if(temp.equalsIgnoreCase("qq")) {//如果是qq
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_QQ);
//						} else if(temp.equalsIgnoreCase("yahoo")) {//如果是yahoo
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_YAHOO);
//						} else if(temp.equalsIgnoreCase("custom")) {//如果是custom
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_CUSTOM);
//						} else if(temp.equalsIgnoreCase("googleTalk")) {//如果是googleTalk
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_GOOGLE_TALK);
//						} else if(temp.equalsIgnoreCase("icq")) {//如果icq
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_ICQ);
//						} else if(temp.equalsIgnoreCase("jabber")) {//如果jabber
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_JABBER);
//						} else if(temp.equalsIgnoreCase("netmeeting")) {//如果netmeeting
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_NETMEETING);
//						} else if(temp.equalsIgnoreCase("skype")) {//如果skype
//							builder.withValue(Im.PROTOCOL, Im.PROTOCOL_SKYPE);
//						} else {//其他
//							builder.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_OTHER);
//						}
//						ops.add(builder.build());
//					}
//				}
//			}
//
//			//插入网站地址 urls
//			if (!Utils.isEmpty(urls)) {
//				for (String key : urls.keySet()) {
//					if (!Utils.isEmpty(urls.get(key))) {
//						builder = ContentProviderOperation.newInsert(uri);
//						builder.withValueBackReference(Data.RAW_CONTACT_ID, rawIndex)
//							.withValue(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE)
//							.withValue(Website.URL, urls.get(key).toString())
//							.withYieldAllowed(true);
//						String temp = key.split("_")[0];
//						if(temp.equalsIgnoreCase("custom")) {//如果是custom web地址
//							builder.withValue(Website.TYPE, Website.TYPE_CUSTOM);
//						} else if(temp.equalsIgnoreCase("home")) {//如果是home web地址
//							builder.withValue(Website.TYPE, Website.TYPE_HOME);
//						} else if(temp.equalsIgnoreCase("homePage")) {//如果是homePage web地址
//							builder.withValue(Website.TYPE, Website.TYPE_HOMEPAGE);
//						} else if(temp.equalsIgnoreCase("work")) {//如果是work web地址
//							builder.withValue(Website.TYPE, Website.TYPE_WORK);
//						} else if(temp.equalsIgnoreCase("ftp")) {//如果是ftp web地址
//							builder.withValue(Website.TYPE, Website.TYPE_FTP);
//						} else if(temp.equalsIgnoreCase("blog")) {//如果是blog web地址
//							builder.withValue(Website.TYPE, Website.TYPE_BLOG);
//						} else {//其他
//							builder.withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_OTHER);
//						}
//						ops.add(builder.build());
//					}
//				}
//			}
//
//			if (!Utils.isEmpty(groups)) {
//				//循环组，先判断是否组存在，如果不存在，创建组
//				for (String key : groups.keySet()) {
//					String val = groups.get(key).toString();
//					long id = -1;
//					//如果key是个id，表示组已经存在
//					if (Utils.isNumber(val)) {
//						id = Long.parseLong(val);
//					} else if (!Utils.isEmpty(val)) {
//						//如果组不存在，判断组的名字是否为空，不为空，查询是否存在，不存在创建
//						id = createGroup(val);
//					}
//					if (id != -1) {
//						builder = ContentProviderOperation.newInsert(uri);
//						builder.withValueBackReference(GroupMembership.RAW_CONTACT_ID, rawIndex);
//						// 给组添加成员(groupId, personId)
//						builder.withValue(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE)
//								.withValue(GroupMembership.GROUP_ROW_ID, id)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(builder.build());
//					}
//				}
//			}
//			Log.i(Const.APPTAG, ">>==" +  ops.toString() );
//			//批量执行插入
//			try {
//				ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, ops);
//				//插入成功返回的Uri集合
//				Map<Long, String> uris = new HashMap<Long, String>();
//				for (ContentProviderResult result : results) {
//					Log.i(Const.APPTAG, result.toString());
//					if (result.uri != null) {
//						uris.put(ContentUris.parseId(result.uri), result.uri.toString());
//					}
//				}
//				if (uris.size() > 0) {
//					addResult.put("result", "1");
//					addResult.put("obj", uris);
//				}
//			} catch (Exception e) {
//				Log.i(Const.APPTAG, e.getMessage());
//				addResult.put("result", "-1");
//				addResult.put("obj", "插入失败:" + e.getMessage());
//			}
//		}
//
//		if (addResult.size() == 0) {
//			addResult.put("result", "0");
//			addResult.put("obj", "无效插入，联系人信息不完整！");
//		}
//		return addResult;
//	}
//
//	/**
//	 * 根据联系人id查询联系人详细信息
//	 *
//	 * @param contactId
//	 * @return HashMap<String, String>
//	 *         <p>
//	 *         查询的所有字段：data_version, phonetic_name, data_set, phonetic_name_style, contact_id, lookup, data12, data11, data10,
//	 *         mimetype, data15, data14, data13, display_name_source, photo_uri, data_sync1, data_sync3, data_sync2, contact_chat_capability,
//	 *         data_sync4, account_type, account_type_and_data_set, custom_ringtone, photo_file_id, has_phone_number, nickname,
//	 *         status, data1, chat_capability, data4, data5, data2, data3, data8, data9, data6, group_sourceid, account_name, data7,
//	 *         display_name, raw_contact_is_user_profile, in_visible_group, display_name_alt, company, contact_account_type,
//	 *         contact_status_res_package, is_primary, contact_status_ts,  raw_contact_id, times_contacted, contact_status,
//	 *         status_res_package, status_icon, contact_status_icon, version, mode, last_time_contacted, res_package, _id, name_verified,
//	 *         dirty, status_ts, is_super_primary, photo_thumb_uri, photo_id, send_to_voicemail, name_raw_contact_id, contact_status_label,
//	 *         status_label, sort_key_alt, starred, sort_key, contact_presence, sourceid
//	 *         </p>
//	 *         <p>
//	 *         Phone.TYPE: TYPE_HOME = 1;      TYPE_MOBILE = 2;        TYPE_WORK = 3;
//	 *         			   TYPE_FAX_WORK = 4;  TYPE_FAX_HOME = 5;      TYPE_PAGER = 6;
//	 *                     TYPE_OTHER = 7;     TYPE_CALLBACK = 8; 	   TYPE_CAR = 9;
//	 *                 TYPE_COMPANY_MAIN = 10; TYPE_ISDN = 11;         TYPE_MAIN = 12;
//	 *                 TYPE_OTHER_FAX = 13;    TYPE_RADIO = 14;        TYPE_TELEX = 15;
//	 *                 TYPE_TTY_TDD = 16;      TYPE_WORK_MOBILE = 17;  TYPE_WORK_PAGER = 18;
//	 *                 TYPE_ASSISTANT = 19;    TYPE_MMS = 20;
//	 *         </p>
//	 */
//	public HashMap<String, Object> getContactById(String contactId) {
//		HashMap<String, Object> contact = new HashMap<String, Object>();
//
//		Cursor ctCursor = resolver.query(Contacts.CONTENT_URI, new String[]{
//				Contacts._ID,
////				Contacts.DISPLAY_NAME,
//				Contacts.SORT_KEY_PRIMARY,
//				Contacts.LAST_TIME_CONTACTED,
//				}, Contacts._ID + "=" + contactId, null, null);
//
//		while (ctCursor.moveToNext()) {
//			String id = ctCursor.getString(0);
////			String displayName = ctCursor.getString(1);
//			String sortKey = ctCursor.getString(1);
//			String lastTime = ctCursor.getString(2);
//
//			contact.put("id", id);
//			contact.put("sortKey", sortKey);
////			contact.put("displayName", displayName);
//
//			contact.put("lastTime", lastTime);
//		}
//		ctCursor.close();
//
//		getContactsData(contactId, contact);
//		return contact;
//	}
//
//	/**
//	 * 获取联系人详细信息（电话、email、im、网站、地址、分组）
//	 * @param contactId
//	 * @param contact
//	 */
//	private void getContactsData(String contactId, Map<String, Object> contact) {
//		HashMap<String, Object> phones = new HashMap<String, Object>();
//		HashMap<String, Object> emails = new HashMap<String, Object>();
//		HashMap<String, Object> address = new HashMap<String, Object>();
//		HashMap<String, Object> ims = new HashMap<String, Object>();
//		HashMap<String, Object> urls = new HashMap<String, Object>();
//		HashMap<String, Object> groups = new HashMap<String, Object>();
//
//		//如果要获得data表中某个id对应的数据，则URI为content://com.android.contacts/contacts/#/data
//		Uri contactsUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(contactId));
//		Uri uri = Uri.withAppendedPath(contactsUri, Contacts.Data.CONTENT_DIRECTORY);
//
//		 //Data表： data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等
//		//PHOTO_FILE_ID = DATA14; PHOTO = DATA15; 照片头像
//		//new String[] { Data._ID, Data.DATA1, Data.DATA15, Data.MIMETYPE }
//		Cursor cursor = resolver.query(uri, null, null, null, null);
//
//		//查询联系人个人信息
//		while (cursor.moveToNext()) {
//			String id = cursor.getString(cursor.getColumnIndex(Data._ID));
//			String mimeType = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE));
//			String data1 = cursor.getString(cursor.getColumnIndex(Data.DATA1));
//			int data2 = cursor.getInt(cursor.getColumnIndex(Data.DATA2));
//
//			// 这些类都在  android.provider.ContactsContract.CommonDataKinds 下
//			// vnd.android.cursor.item/name 如果是姓名
//			if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
//				String familyName = cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME));
//				String middleName = cursor.getString(cursor.getColumnIndex(StructuredName.MIDDLE_NAME));
//				String givenName = cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME));
//				String prefix = cursor.getString(cursor.getColumnIndex(StructuredName.PREFIX));
//				String suffix = cursor.getString(cursor.getColumnIndex(StructuredName.SUFFIX));
//				String phoneticName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_NAME));
//				String phoneticFamilyName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
//				String phoneticMiddleName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
//				String phoneticGivenName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
//
//				contact.put("diplayName", data1);//显示名称（前缀+中缀+姓+中间名+名+后缀）
//				contact.put("familyName", familyName);//姓
//				contact.put("middleName", middleName);//中间名
//				contact.put("givenName", givenName);//名
//				contact.put("prefix", prefix);//前缀
//				contact.put("suffix", suffix);//后缀
//				contact.put("phoneticName", phoneticName);//全拼音
//				contact.put("phoneticFamilyName", phoneticFamilyName);//姓拼音
//				contact.put("phoneticMiddleName", phoneticMiddleName);//中间名拼音
//				contact.put("phoneticGivenName", phoneticGivenName);//名拼音
//				if (Utils.isEmpty(data1)) {
//					data1 = "";
//				}
//				contact.put("phoneticFullname", Utils.getPinYinHeadChar(data1));
//
//			}// 获取昵称信息
//			else if (mimeType.equals(Nickname.CONTENT_ITEM_TYPE)) {
//					String nickName = cursor.getString(cursor.getColumnIndex(Nickname.NAME));
//					contact.put("nickName", nickName);
//			}//vnd.android.cursor.item/phone_v2 如果是电话
//			else if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
//				switch (data2) {
//					case Phone.TYPE_MOBILE://手机号码
//						phones.put("mobile_" + id, data1);
//						break;
//					case Phone.TYPE_WORK://公司电话
//						phones.put("work_" + id, data1);
//						break;
//					case Phone.TYPE_WORK_MOBILE://工作手机
//						phones.put("workMobile_" + id, data1);
//						break;
//					case Phone.TYPE_WORK_PAGER://工作寻呼机
//						phones.put("workPager_" + id, data1);
//						break;
//					case Phone.TYPE_FAX_WORK://工作传真
//						phones.put("faxWork_" + id, data1);
//						break;
//					case Phone.TYPE_HOME://家庭电话
//						phones.put("home_" + id, data1);
//						break;
//					case Phone.TYPE_FAX_HOME://家庭传真
//						phones.put("faxHome_" + id, data1);
//						break;
//					case Phone.TYPE_PAGER://寻呼机
//						phones.put("pager_" + id, data1);
//						break;
//					case Phone.TYPE_CALLBACK://回拨号码
//						phones.put("callback_" + id, data1);
//						break;
//					case Phone.TYPE_COMPANY_MAIN://公司总机
//						phones.put("companyMain_" + id, data1);
//						break;
//					case Phone.TYPE_CAR://车载电话
//						phones.put("car_" + id, data1);
//						break;
//					case Phone.TYPE_ISDN://ISDN
//						phones.put("isdn_" + id, data1);
//						break;
//					case Phone.TYPE_MAIN://总机
//						phones.put("main_" + id, data1);
//						break;
//					case Phone.TYPE_RADIO://无线装置
//						phones.put("radio_" + id, data1);
//						break;
//					case Phone.TYPE_TELEX://电报
//						phones.put("telex_" + id, data1);
//						break;
//					case Phone.TYPE_TTY_TDD://TTY_TDD
//						phones.put("ttyTdd_" + id, data1);
//						break;
//					case Phone.TYPE_ASSISTANT:// 助理
//						phones.put("assistant_" + id, data1);
//						break;
//					case Phone.TYPE_MMS://彩信
//						phones.put("mms_" + id, data1);
//						break;
//					default://其他号码
//						phones.put("other_" + id, data1);
//						break;
//				}
//			}//vnd.android.cursor.item/email_v2 如果是电子邮件 ，怎么会保存在 家用字段
//			else if (mimeType.equals(Email.CONTENT_ITEM_TYPE)) {
//
//				switch (data2) {
//					case Email.TYPE_WORK://工作邮件
//						emails.put("work_" + id, data1);
//						break;
//					case Email.TYPE_HOME://家庭邮件
//						emails.put("home_" + id, data1);
//						break;
//					case Email.TYPE_CUSTOM://自定义邮件地址
//						emails.put("custom_" + id, data1);
//						break;
//					case Email.TYPE_MOBILE://手机邮件地址
//						emails.put("mobile_" + id, data1);
//						break;
//					default://其他
//						emails.put("other_" + id, data1);
//						break;
//				}
//			}//vnd.android.cursor.item/email_v2如果是地址
//			else if (mimeType.equals(StructuredPostal.CONTENT_ITEM_TYPE)) {
//				switch (data2) {
//					case StructuredPostal.TYPE_HOME://家庭地址
//						address.put("home_" + id, data1);
//						break;
//					case StructuredPostal.TYPE_WORK://工作公司地址
//						address.put("work_" + id, data1);
//						break;
//					case StructuredPostal.TYPE_CUSTOM://自定义地址
//						address.put("custom_" + id, data1);
//						break;
//					default://其他地址
//						address.put("other_" + id, data1);
//						break;
//				}
//
//			}// 获取组织信息
//			else if(mimeType.equals(Organization.CONTENT_ITEM_TYPE)){
//				// 取出组织类型
//				int orgType = cursor.getInt(cursor.getColumnIndex(Organization.TYPE));
//				// 单位
////				if (orgType == Organization.TYPE_CUSTOM) {
//				if (orgType == Organization.TYPE_WORK) {
//					String company = cursor.getString(cursor.getColumnIndex(Organization.COMPANY));
//					contact.put("company", company);
//					String job = cursor.getString(cursor.getColumnIndex(Organization.TITLE));
//					contact.put("job", job);
//					String department = cursor.getString(cursor.getColumnIndex(Organization.DEPARTMENT));
//					contact.put("department", department);
//				}
//
//			}//获取生日
//			else if(mimeType.equals(Event.CONTENT_ITEM_TYPE)){
//				// 生日
//				switch (data2) {
//					case Event.TYPE_BIRTHDAY:
//						contact.put("birthday", data1);
//					break;
//					case Event.TYPE_ANNIVERSARY:
//						contact.put("anniversary", data1);
//					break;
////					default://其他
////						contact.put("otherDate_" + id, data1);
////						break;
//				}
//			}// 获取备注信息
//			else if (mimeType.equals(Note.CONTENT_ITEM_TYPE)) {
//				String note = cursor.getString(cursor.getColumnIndex(Note.NOTE));
//				contact.put("note", note);
//			}// 即时消息 ims
//			else if (mimeType.equals(Im.CONTENT_ITEM_TYPE)) {
//					// 取出即时消息类型
//					int protocal = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
//					switch (protocal) {
//						case Im.PROTOCOL_AIM://aim
//							ims.put("aim_" + id, data1);
//							break;
//						case Im.PROTOCOL_MSN://msn
//							ims.put("msn_" + id, data1);
//							break;
//						case Im.PROTOCOL_QQ://QQ
//							ims.put("qq_" + id, data1);
//							break;
//						case Im.PROTOCOL_YAHOO://YAHOO
//							ims.put("yahoo_" + id, data1);
//							break;
//						case Im.PROTOCOL_CUSTOM://CUSTOM
//							ims.put("custom_" + id, data1);
//						case Im.PROTOCOL_GOOGLE_TALK://GOOGLE_TALK
//							ims.put("googleTalk_" + id, data1);
//							break;
//						case Im.PROTOCOL_ICQ://ICQ
//							ims.put("icq_" + id, data1);
//							break;
//						case Im.PROTOCOL_JABBER://JABBER
//							ims.put("jabber_" + id, data1);
//							break;
//						case Im.PROTOCOL_NETMEETING://NETMEETING
//							ims.put("netmeeting_" + id, data1);
//							break;
//						case Im.PROTOCOL_SKYPE://SKYPE
//							ims.put("skype_" + id, data1);
//							break;
//						default://其他
//							ims.put("other_" + id, data1);
//							break;
//					}
//				} // 获取网站信息
//				else if (mimeType.equals(Website.CONTENT_ITEM_TYPE)) {
//					// 取出网站类型
//					int webType = cursor.getInt(cursor.getColumnIndex(Website.TYPE));
//					switch (webType) {
//						case Website.TYPE_CUSTOM://CUSTOM
//							urls.put("custom_" + id, data1);
//							break;
//						case Website.TYPE_HOME://HOME
//							urls.put("home_" + id, data1);
//							break;
//						case  Website.TYPE_HOMEPAGE://HOMEPAGE
//							urls.put("homePage_" + id, data1);
//							break;
//						case Website.TYPE_WORK://WORK
//							urls.put("work_" + id, data1);
//							break;
//						case Website.TYPE_FTP://FTP
//							urls.put("ftp_" + id, data1);
//							break;
//						case Website.TYPE_BLOG://BLOG博客
//							urls.put("blog_" + id, data1);
//							break;
//						default://其他
//							urls.put("other_" + id, data1);
//							break;
//					}
//				}
////			vnd.android.cursor.item/photo 如果是照片
////			} else if (mimeType.equals(Photo.CONTENT_ITEM_TYPE)) {
////				// Photo.PHOTO = Data.DATA15
////
////				byte[] data = cursor.getBlob(cursor.getColumnIndex(Photo.PHOTO));
////				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
////			}
////			vnd.android.cursor.item/organization如果是组织
////			else if (mimeType.equals(Organization.CONTENT_ITEM_TYPE)) { }
//		}
//		cursor.close();
//		//如果查询到数据
//		if (contact.size() > 0 || phones.size() > 0 ||  emails.size() > 0 || address.size() > 0
//				|| ims.size() > 0 || urls.size() > 0) {
//			contact.put("contactId", contactId);
//			//查询联系人所在的组
//			Cursor gmsCursor = resolver.query(
//					Data.CONTENT_URI,
//					new String[]{ Data._ID, GroupMembership.GROUP_ROW_ID },
//					GroupMembership.MIMETYPE + "=? and " + Data.RAW_CONTACT_ID + "=?",
//					new String[]{ GroupMembership.CONTENT_ITEM_TYPE, contactId },
//					null);
//			while (gmsCursor.moveToNext()) {
//				int _id = gmsCursor.getInt(0);
//				int groupId = gmsCursor.getInt(1);
//				Cursor groupCursor = resolver.query(
//						Groups.CONTENT_URI,
//						new String[]{ Groups._ID, Groups.TITLE},
//						Groups._ID + "=" + groupId,
//						null,
//						null);
//				if(groupCursor.moveToFirst()){
//					groups.put("title" + _id + "_" + groupId, groupCursor.getString(1));
//		//			groups.put(groups.size() + ":id,title", groupId + "," + groupCursor.getString(1));
//				}
//				groupCursor.close();
//			}
//			gmsCursor.close();
//
//			contact.put("groups", groups);
//			contact.put("phones", phones);
//			contact.put("emails", emails);
//			contact.put("addresses", address);
//			contact.put("ims", ims);
//			contact.put("urls", urls);
//		}
//	}
//
//	/**
//	 * 查询联系人
//	 * @param 姓名模糊查询-根据分组查询-根据电话号码(完全匹配)查询
//	 * @param selections[name=v,groupId=v,phone=v]
//	 * @return List<Long> contactId
//	 */
//	public Set<String> query(Map<String, String> selections){
//		Set<String> ids = new HashSet<String>();
//		String groupId 		= null;
//		String displayName 	= null;
//		String phone 		= null;
//		if (!Utils.isEmpty(selections)) {
//			groupId 		= selections.get("groupId");//分组id
//			displayName 	= selections.get("displayName");//姓名
//			phone 			= selections.get("phoneNumber");//电话
//		}
//
//		Log.i(Const.APPTAG, "查询联系人条件：groupId=" + groupId + ";displayName=" + displayName + ";phone=" + phone);
//		//查询条件为空代表查询所有联系人
//		if (selections == null || selections.size() == 0 ||
//				(!Utils.isNumber(groupId) && Utils.isEmpty(displayName) && Utils.isEmpty(phone)) ) {
//			//管理联系人的uri
//			Uri uri = Contacts.CONTENT_URI;
//			Cursor cursor = null;
//
//			cursor = resolver.query(uri, new String[]{ Contacts._ID, Contacts.DISPLAY_NAME}, null, null,
//					Contacts.SORT_KEY_PRIMARY + " asc");
//			//循环查询结果 cursor
//			while (cursor.moveToNext()) {
//				ids.add(cursor.getString(cursor.getColumnIndex(Contacts._ID)));
//			}
//			cursor.close();
//		} else {
//			if (groupId != null && Utils.isNumber(groupId)) {
//				Cursor groupCursor = null;
//				//根据组id查询关系表
//				groupCursor = resolver.query(
//						Data.CONTENT_URI,
//						new String[] { Data.RAW_CONTACT_ID },
//						Data.MIMETYPE + "=? AND " + GroupMembership.GROUP_ROW_ID + "=?",
//						new String[] { GroupMembership.CONTENT_ITEM_TYPE, groupId },
//						null);
//				StringBuffer selection = new StringBuffer(Data.RAW_CONTACT_ID + " in(");
//				//如果有这个组，在组里,根据条件查询联系人
//				List<String> selectionArgs = new ArrayList<String>();
//				while (groupCursor.moveToNext()) {
//					selection.append("?,");
//					selectionArgs.add(groupCursor.getInt(0) + "");
//				}
//				groupCursor.close();
//				selection = new StringBuffer(selection.substring(0, selection.length() - 1) + ")");
//
//				queryContactIds(ids, displayName, phone, selection, selectionArgs);
//			} else {
//				StringBuffer selection = new StringBuffer();
//				List<String> selectionArgs = new ArrayList<String>();
//
//				queryContactIds(ids, displayName, phone, selection, selectionArgs);
//			}
//
//		}
//		return ids;
//	}
//
//	/**
//	 * 根据姓名，电话查询联系人
//	 * @param ids
//	 * @param name
//	 * @param phone
//	 * @param selection
//	 * @param selectionArgs
//	 */
//	private void queryContactIds(Set<String> ids, String name, String phone,
//			StringBuffer selection, List<String> selectionArgs) {
//		if (name != null && !name.equals("")) {
//			selection.append(selection.length() > 0 ? " AND " : "");
//			selection.append(StructuredName.DISPLAY_NAME + " LIKE ? ");
//			selectionArgs.add("%" + name + "%");
//		}
//		if (phone != null && !phone.equals("")) {
//			selection.append(selection.length() > 0 ? " OR " : "");
//			selection.append(Phone.NUMBER + " = ?  ");
//			selectionArgs.add(phone);
//		}
//
//		Log.i(Const.APPTAG, "查询联系人-where：" + selection.toString() + ";val:" + selectionArgs.toString());
//
//		Cursor contactCursor = resolver.query(
//				Data.CONTENT_URI,
//				new String[] { Data.RAW_CONTACT_ID, StructuredName.DISPLAY_NAME },
//				selection.toString(),
//				selectionArgs.toArray(new String[]{}),
//				null);
//		while (contactCursor.moveToNext()) {
//			ids.add(contactCursor.getString(contactCursor.getColumnIndex(Data.RAW_CONTACT_ID)));
//		}
//		contactCursor.close();
//	}
//	/**
//	 * 获得所有联系人的id
//	 * @return List<String>
//	 */
//	public List<String> getContactsIds(){
//
//		List<String> ids = new ArrayList<String>();
//
//		Uri uri = Contacts.CONTENT_URI;
//
//		Cursor cursor = resolver.query(uri, new String[]{ Contacts._ID }, null, null, null);
//		while (cursor.moveToNext()) {
//			ids.add(cursor.getString(0));
//		}
//		cursor.close();
//
//		return ids;
//	}
//	/**
//	 * 查询所有联系人（id、姓名、所有电话）
//	 * @param luaStateWrapper
//	 * @return List<Map<String, Object>>
//	 */
//	public List<Map<String, Object>> getContacts(){
//
//		List<Map<String, Object>> listContacts = new ArrayList<Map<String,Object>>();
//
//		Uri uri = Contacts.CONTENT_URI;
//
//		Cursor cursor = resolver.query(uri, new String[]{
//				Contacts._ID,
////				Contacts.DISPLAY_NAME,
//				Contacts.SORT_KEY_PRIMARY,
//				Contacts.LAST_TIME_CONTACTED,}, null, null, null);
//
//		Map<String, Object> ct = null;
////		String displayName = null;
//
//		while (cursor.moveToNext()) {
//			ct = new HashMap<String, Object>();
//			String id = cursor.getString(0);
////			displayName = cursor.getString(1);
//			String sortKey = cursor.getString(1);
//			String lastTime = cursor.getString(2);
//
//			ct.put("id", id);
////			ct.put("displayName", displayName);
//			ct.put("sortKey", sortKey);
//			ct.put("lastTime", lastTime);
//
//			listContacts.add(ct);
//		}
//		cursor.close();
//
//		for (Map<String, Object> contact : listContacts) {
//			String contactId = contact.get("contactId").toString();
//			getContactsData(contactId, contact);
//		}
//		return listContacts;
//	}
//
////	/**
////	 * 根据联系id查询所有的号码
////	 * @param contactId
////	 * @return HashMap<String, Object>
////	 */
////	public HashMap<String, Object> getPhonesByContactId(String contactId) {
////		HashMap<String, Object> phones = new HashMap<String, Object>();
////
////		//如果要获得data表中某个id对应的数据，则URI为content://com.android.contacts/contacts/#/data
////		Uri contactsUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(contactId));
////		Uri uri = Uri.withAppendedPath(contactsUri, Contacts.Data.CONTENT_DIRECTORY);
////
////		Cursor cursor = resolver.query(uri, null, null, null, null);
////
////		//查询联系人个人信息
////		while (cursor.moveToNext()) {
////			String id = cursor.getString(cursor.getColumnIndex(Data._ID));
////			String mimeType = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE));
////			String data1 = cursor.getString(cursor.getColumnIndex(Data.DATA1));
////			int data2 = cursor.getInt(cursor.getColumnIndex(Data.DATA2));
////
////			if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
////				switch (data2) {
////					case Phone.TYPE_MOBILE://手机号码
////						phones.put("mobile_" + id, data1);
////						break;
////					case Phone.TYPE_WORK://公司电话
////						phones.put("work_" + id, data1);
////						break;
////					case Phone.TYPE_FAX_WORK://工作传真
////						phones.put("faxWork_" + id, data1);
////						break;
////					case Phone.TYPE_HOME://家庭电话
////						phones.put("home_" + id, data1);
////						break;
////					case Phone.TYPE_OTHER://其他号码
////					default:
////						phones.put("other_" + id, data1);
////						break;
////				}
////			}
////		}
////		cursor.close();
////		return phones;
////	}
////
//	/**
//	 * 获取所有的组
//	 * @return
//	 */
//	public List<HashMap<String, String>> getGroups() {
//		List<HashMap<String, String>> groups = new ArrayList<HashMap<String,String>>();
//		Cursor cursor = resolver.query(Groups.CONTENT_URI, null, null, null, null);
//		HashMap<String, String> group = new HashMap<String, String>();
//		while (cursor.moveToNext()) {
//			int id = cursor.getInt(cursor.getColumnIndex(Groups._ID));
//			String title = cursor.getString(cursor.getColumnIndex(Groups.TITLE));
//			group.put("id", id + "");
//			group.put("title", title);
//			groups.add(group);
//		}
//		cursor.close();
//		return groups;
//	}
//
//	/**
//	 * 创建组
//	 * @return int
//	 */
//	public long createGroup(String title) {
//		if (Utils.isEmpty(title)) {
//			return -1;
//		}
//		long gId = getGroupByTitle(title);
//		if (gId == -1) {
//			ContentValues values = new ContentValues();
//			values.put(Groups.TITLE, title);
//			Uri uri = resolver.insert(Groups.CONTENT_URI, values);
//			gId = ContentUris.parseId(uri);
//		}
//		return gId;
//	}
//	/**
//	 * 根据组的名称查询组
//	 * @return int
//	 */
//	public int getGroupByTitle(String title) {
//		int id = -1;
//		Cursor cursor = resolver.query(
//				Groups.CONTENT_URI,
//				new String[] { Groups._ID },
//				Groups.TITLE + "='" + title + "'",
//				null, null);
//		if(cursor.moveToNext()) {
//			id = cursor.getInt(cursor.getColumnIndex(Groups._ID));
//		}
//		cursor.close();
//		return id;
//	}
//	/**
//	 * 根据组的id删除组
//	 * @return int
//	 */
//	public int delGroupById(String selection, String[] ids) {
//		Uri uri = Uri.parse(Groups.CONTENT_URI + "?" + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
//		int i = resolver.delete(
//				uri,
//				Groups._ID + selection ,
//				ids);
//		return i;
//	}
//
//	/**
//	 * 删除全部联系人
//	 * @return
//	 */
//	public HashMap<String, Object> delAllContacts() {
//		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//		ContentProviderOperation op = null;
//		Uri uri = null;
//		HashMap<String, Object> delResult = new HashMap<String, Object>();
//		int num = 0;//删除影响的行数
//		resolver.delete(Uri.parse(ContactsContract.RawContacts.CONTENT_URI.toString() + "?"
//				+ ContactsContract.CALLER_IS_SYNCADAPTER + "=true"),
//				ContactsContract.RawContacts._ID + ">0", null);
//		//删除Data表的数据
//		uri = Uri.parse(Data.CONTENT_URI.toString() + "?" + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
//		op = ContentProviderOperation.newDelete(uri)
//				.withSelection(Data.RAW_CONTACT_ID + ">0", null)
//				.withYieldAllowed(true)
//				.build();
//		ops.add(op);
//		//删除RawContacts表的数据
//		uri = Uri.parse(RawContacts.CONTENT_URI.toString() + "?" + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
//		op = ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
//				.withSelection(RawContacts._ID + ">0", null)
//				.withYieldAllowed(true)
//				.build();
//		ops.add(op);
//		//删除Contacts表的数据
//		uri = Uri.parse(Contacts.CONTENT_URI.toString() + "?" + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
//		op = ContentProviderOperation.newDelete(uri)
//				.withSelection(Contacts._ID + ">0", null)
//				.withYieldAllowed(true)
//				.build();
//		ops.add(op);
//		//执行批量删除
//		try {
//			ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, ops);
//			for (ContentProviderResult result : results) {
//				num += result.count;
//				Log.i(Const.APPTAG, "删除影响的行数：" + result.count);
//			}
//			delResult.put("result", "1");
//			delResult.put("obj",  num);
//		} catch (Exception e) {
//			Log.i(Const.APPTAG, e.getMessage());
//			delResult.put("result", "-1");
//			delResult.put("obj", "删除失败！" + e.getMessage());
//		}
//		if (delResult.size() == 0) {
//			delResult.put("result", "0");
//			delResult.put("obj", "无效删除，联系人信息不正确！");
//		}
//		return delResult;
//	}
//
//	/**
//	 * 删除联系人 ，联系人相关联的有三张表，将三张表的数据全部删除
//     * 部分手机删除第二张即可，部分手机需要删除三张表
//     * resolver.delete(ContactsContract.Data.CONTENT_URI, null, null);
//     * resolver.delete(ContactsContract.RawContacts.CONTENT_URI, null, null);
//     * resolver.delete(ContactsContract.Contacts.CONTENT_URI, null, null);
//	 * @param contactId
//	 * @param groupId
//	 * @return
//	 * 如果删除成功：(1, num);
//	 * 如果删除失败：(-1, "删除失败:" + e.getMessage());
//	 * 如果信息无效：(0, "无效删除，联系人信息不正确！");
//	 */
//	public HashMap<String, Object> delContacts(List<String> contactIds) {
//		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//		ContentProviderOperation op = null;
//		Uri uri = null;
//
//		HashMap<String, Object> delResult = new HashMap<String, Object>();
//		int num = 0;//删除影响的行数
//		if (Utils.isEmpty(contactIds)) {
//			delResult.put("result", "0");
//			delResult.put("obj", "无效删除，联系人id不正确！");
//			return delResult;
//		}
//		List<String> ids = new ArrayList<String>();
//		String selection = " in(";
//		for (String contactId : contactIds) {
//			//如果联系人id 不大于 0,或者不存在，循环下一次
//			if (!Utils.isNumber(contactId) || Long.parseLong(contactId) <= 0 || !isExistContact(contactId)) {
//				continue;
//			}
//			selection += "?,";
//			ids.add(contactId);
//		}
//		if (ids.size() == 0) {
//			delResult.put("result", "0");
//			delResult.put("obj", "无效联系人id");
//			return delResult;
//		}
//		selection = selection.substring(0, selection.length() - 1) + ")";
//		String[] selectionArgs = ids.toArray(new String[]{});
//
//		//删除Data表的数据
//		uri = Uri.parse(Data.CONTENT_URI.toString() + "?" + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
//		op = ContentProviderOperation.newDelete(uri)
//				.withSelection(Data.RAW_CONTACT_ID + selection, selectionArgs)
//				.withYieldAllowed(true)
//				.build();
//		ops.add(op);
//		//删除RawContacts表的数据
//		uri = Uri.parse(RawContacts.CONTENT_URI.toString() + "?" + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
//		op = ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
//				.withSelection(RawContacts._ID + selection, selectionArgs)
//				.withYieldAllowed(true)
//				.build();
//		ops.add(op);
//		//删除Contacts表的数据
//		uri = Uri.parse(Contacts.CONTENT_URI.toString() + "?" + ContactsContract.CALLER_IS_SYNCADAPTER + "=true");
//		op = ContentProviderOperation.newDelete(uri)
//				.withSelection(Contacts._ID + selection, selectionArgs)
//				.withYieldAllowed(true)
//				.build();
//		ops.add(op);
//
//		//执行批量删除
//		try {
//			ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, ops);
//			for (ContentProviderResult result : results) {
//				num += result.count;
//				Log.i(Const.APPTAG, "删除影响的行数：" + result.count);
//			}
//			delResult.put("result", "1");
//			delResult.put("obj",  num);
//		} catch (Exception e) {
//			Log.i(Const.APPTAG, e.getMessage());
//			delResult.put("result", "-1");
//			delResult.put("obj", "删除失败！" + e.getMessage());
//		}
//		if (delResult.size() == 0) {
//			delResult.put("result", "0");
//			delResult.put("obj", "无效删除，联系人id不正确！");
//		}
//		return delResult;
//	}
//
//
//	/**
//	 * 	如果组的id>0: 直接根据组的id删除组内所有联系人；
//	 *	如果参数二为 true :则连组一起删除；
//	 * @param groupId
//	 * @param isDelGroup
//	 * @return
//	 */
//	public HashMap<String, Object> delContactsByGroup(List<String> groupIds, boolean isDelGroup) {
//		HashMap<String, Object> result = new HashMap<String, Object>();
//
//		if (Utils.isEmpty(groupIds)) {
//			result.put("result", "0");
//			result.put("obj", "无效的组id");
//			return result;
//		}
//		List<String> contactIds = new ArrayList<String>();
//		List<String> delIds = new ArrayList<String>();
//		String selection = " in(";
//		for (String groupId : groupIds) {
//			//如果联系人id 不大于 0,或者不存在，循环下一次
//			if (!Utils.isNumber(groupId) || Long.parseLong(groupId) <= 0 || !isExistGroup(groupId)) {
//				continue;
//			}
//			delIds.add(groupId);
//			selection += "?,";
//		}
//
//		if (delIds.size() > 0) {
//			//查询组内的联系人
//			selection = selection.substring(0, selection.length() - 1) + ")";
//			String[] selectionArgs = delIds.toArray(new String[]{});
//
//			contactIds.addAll(getCotactsByGroup(selection, selectionArgs));
//			if (contactIds.size() > 0) {
//				//删除组内的联系人
//				result = delContacts(contactIds);
//				Log.i(Const.APPTAG, selection + ";value:" + selectionArgs.length);
//				//删除组
//				if (result.get("result") != null && isDelGroup) {
//					int n = Integer.parseInt(result.get("obj").toString()) + delGroupById(selection, selectionArgs);
//					result.put("result", "1");
//					result.put("obj", n);
//				}
//			}
//		} else {
//			result.put("result", "-1");
//			result.put("obj", "无效的组id");
//		}
//		return result;
//	}
//
//	/**
//	 * 根据多个组的id,查询组内的所有联系人
//	 */
//	public List<String> getCotactsByGroup(String selection, String[] groupIds) {
//		List<String> list = new ArrayList<String>();
//
//		Cursor groupContactCursor = resolver.query(
//						Data.CONTENT_URI,
//						new String[] { Data.RAW_CONTACT_ID },
//						GroupMembership.GROUP_ROW_ID + selection + " AND "
//							+ Data.MIMETYPE + "='" + GroupMembership.CONTENT_ITEM_TYPE + "'",
//						groupIds, null);
//
//		// Second, query the corresponding name of the raw_contact_id
//		while (groupContactCursor.moveToNext()) {
//			Cursor contactCursor = resolver.query(
//					Data.CONTENT_URI,
//					new String[] { Data.RAW_CONTACT_ID},
//					Data.MIMETYPE + "=? AND " + Data.RAW_CONTACT_ID + "=?",
//					new String[]{ StructuredName.CONTENT_ITEM_TYPE, groupContactCursor.getInt(0) + ""},
//					null);
//			if(contactCursor.moveToFirst()){
//				list.add(contactCursor.getString(0));
//			}
//			contactCursor.close();
//		}
//		groupContactCursor.close();
//		return list;
//	}
//
//	/**
//	 * 根据id查询联系人是否存在
//	 */
//	private boolean isExistContact(String id){
//		if (Utils.isEmpty(id)) {
//			return false;
//		}
//		Cursor cursor = resolver.query(
//					Contacts.CONTENT_URI,
//					new String[]{ Contacts._ID},
//					Contacts._ID + " = ? ",
//					new String[]{ id }, null);
//
//		if (cursor.moveToFirst()) {
//			return true;
//		}
//		cursor.close();
//		return false;
//	}
//	/**
//	 * 根据组的id查询组是否存在
//	 * @return boolean
//	 */
//	public boolean isExistGroup(String id) {
//		if (Utils.isEmpty(id)) {
//			return false;
//		}
//		Cursor cursor = resolver.query(
//				Groups.CONTENT_URI,
//				new String[] { Groups._ID },
//				Groups._ID + "=" + id,
//				null, null);
//		if(cursor.moveToFirst()) {
//			return true;
//		}
//		cursor.close();
//		return false;
//	}
//
//	/**
//	 * 批量更新联系人
//	 * @param contacts
//	 * @return
//	 * 如果更新成功：(1, num);
//	 * 如果更新失败：(-1, "更新失败:" + e.getMessage());
//	 * 如果信息无效：(0, "无效更新，联系人信息不完整！");
//	 */
//	public HashMap<String, Object> updateContacts(List<Map<String, Object>> contacts) {
//		HashMap<String, Object> updateResult = new HashMap<String, Object>();
//		if (Utils.isEmpty(contacts)) {
//			updateResult.put("result", "0");
//			updateResult.put("obj", "无效更新，联系人信息不完整！");
//			return updateResult;
//		}
//
//		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//		ContentProviderOperation op = null;
//		int num = 0;//删除影响的行数
//
//		for (Map<String, Object> contact : contacts) {
//
//			String contactId = (String) contact.get("contactId") ;
//			//如果，联系人不存在，查看下一个
//			if (!Utils.isNumber(contactId) || Long.parseLong(contactId) <= 0 || !isExistContact(contactId)) {
//				continue;
//			}
//			String displayName 			= (String) contact.get("displayName");
//			String familyName 			= (String) contact.get("familyName");
//			String middleName 			= (String) contact.get("middleName");
//			String givenName 			= (String) contact.get("givenName");
//			String prefix 				= (String) contact.get("prefix");
//			String suffix 				= (String) contact.get("suffix");
//			String phoneticName 		= (String) contact.get("phoneticName");
//			String phoneticFamilyName 	= (String) contact.get("phoneticFamilyName");
//			String phoneticMiddleName 	= (String) contact.get("phoneticMiddleName");
//			String phoneticGivenName 	= (String) contact.get("phoneticGivenName");
//			String nickName 			= (String) contact.get("nickName");
//			String birthday 			= (String) contact.get("birthday");
//			String anniversary 			= (String) contact.get("anniversary");
//			String note 				= (String) contact.get("note");
//			String company 				= (String) contact.get("company");
//			String job 					= (String) contact.get("job");
//			String department 			= (String) contact.get("department");
//
//			Map<String, Object> phones 	= (Map<String, Object>) contact.get("phones");
//			Map<String, Object> emails 	= (Map<String, Object>) contact.get("emails");
//			Map<String, Object> address = (Map<String, Object>) contact.get("addresses");
//			Map<String, Object> ims 	= (Map<String, Object>) contact.get("ims");
//			Map<String, Object> urls 	= (Map<String, Object>) contact.get("urls");
//			Map<String, Object>	groups 	= (Map<String, Object>) contact.get("groups");
//
//			//如果都为空,循环下一个，联系人信息
//			if (Utils.isEmpty(displayName) && Utils.isEmpty(address) && Utils.isEmpty(phones)
//					&& Utils.isEmpty(emails) && Utils.isEmpty(familyName) && Utils.isEmpty(middleName)
//					&& Utils.isEmpty(givenName) && Utils.isEmpty(prefix) && Utils.isEmpty(suffix)
//					&& Utils.isEmpty(phoneticName) && Utils.isEmpty(phoneticFamilyName) && Utils.isEmpty(phoneticMiddleName)
//					&& Utils.isEmpty(phoneticGivenName) && Utils.isEmpty(nickName) && Utils.isEmpty(birthday)
//					&& Utils.isEmpty(anniversary) && Utils.isEmpty(note) && Utils.isEmpty(company)
//					&& Utils.isEmpty(job) && Utils.isEmpty(department)) {
//				continue;
//			}
//
//			//数据表 uri
//			Uri uri = Data.CONTENT_URI;
//			Builder builder = ContentProviderOperation.newUpdate(uri)
//						.withSelection(Data.RAW_CONTACT_ID + " =? and " + Data.MIMETYPE + " =?",
//								new String[]{ contactId, StructuredName.CONTENT_ITEM_TYPE })
//						.withYieldAllowed(true);
//
//			//更新姓名
//			if (displayName != null) {
//				builder.withValue(StructuredName.DISPLAY_NAME, displayName);
//			}
//			//更新姓
//			if (familyName != null) {
//				builder.withValue(StructuredName.FAMILY_NAME, familyName);
//			}
//			//更新中间名
//			if (middleName != null) {
//				builder.withValue(StructuredName.MIDDLE_NAME, middleName);
//			}
//			//更新名
//			if (givenName != null) {
//				builder.withValue(StructuredName.GIVEN_NAME, givenName);
//			}
//			//更新前缀
//			if (prefix != null) {
//				builder.withValue(StructuredName.PREFIX, prefix);
//			}
//			//更新后缀
//			if (suffix != null) {
//				builder.withValue(StructuredName.SUFFIX, suffix);
//			}
//			//更新全拼
//			if (phoneticName != null) {
//				builder.withValue(StructuredName.PHONETIC_NAME, phoneticName);
//			}
//			//更新姓拼音
//			if (phoneticFamilyName != null) {
//				builder.withValue(StructuredName.PHONETIC_FAMILY_NAME, phoneticFamilyName);
//			}
//			//更新中间名拼音
//			if (phoneticFamilyName != null) {
//				builder.withValue(StructuredName.PHONETIC_MIDDLE_NAME, phoneticMiddleName);
//			}
//			//更新名拼音
//			if (phoneticGivenName != null) {
//				builder.withValue(StructuredName.PHONETIC_GIVEN_NAME, phoneticGivenName);
//			}
//			ops.add(builder.build());
//
//
//			//更新昵称
//			if (nickName != null) {
//				op = ContentProviderOperation.newUpdate(uri)
//						.withSelection(Data.RAW_CONTACT_ID + " =? and " + Data.MIMETYPE + " =?",
//									new String[]{ contactId, Nickname.CONTENT_ITEM_TYPE })
//						.withValue(Nickname.NAME, nickName)
//						.withYieldAllowed(true)
//						.build();
//				ops.add(op);
//			}
//
//			//更新生日
//			if (birthday != null) {
//				op = ContentProviderOperation.newUpdate(uri)
//						.withSelection(
//										Data.RAW_CONTACT_ID + " =? and " +
//										Data.MIMETYPE 		+ " =? and " +
//										Event.TYPE 			+ " =? ",
//									new String[]{
//										contactId,
//										Event.CONTENT_ITEM_TYPE,
//										Event.TYPE_BIRTHDAY + ""})
//						.withValue(Event.START_DATE, birthday)
//						.withYieldAllowed(true)
//						.build();
//				ops.add(op);
//			}
//
//			//更新纪念日
//			if (anniversary != null) {
//				op = ContentProviderOperation.newUpdate(uri)
//						.withSelection(
//										Data.RAW_CONTACT_ID + " =? and " +
//										Data.MIMETYPE 		+ " =? and " +
//										Event.TYPE 			+ " =? ",
//									new String[]{
//										contactId,
//										Event.CONTENT_ITEM_TYPE,
//										Event.TYPE_ANNIVERSARY + ""})
//						.withValue(Event.START_DATE, anniversary)
//						.withYieldAllowed(true)
//						.build();
//				ops.add(op);
//			}
//			//更新备注
//			if (note != null) {
//				op = ContentProviderOperation.newUpdate(uri)
//						.withSelection(Data.RAW_CONTACT_ID + " =? and " + Data.MIMETYPE + " =?",
//									new String[]{ contactId, Note.CONTENT_ITEM_TYPE})
//						.withValue(Note.NOTE, note)
//						.withYieldAllowed(true)
//						.build();
//				ops.add(op);
//			}
//			//更新组织，公司
//			if (company != null || job != null || department != null) {
//				Builder tempBuilder = ContentProviderOperation.newUpdate(uri)
//				.withSelection(
//						Data.RAW_CONTACT_ID + " =? and " +
//						Data.MIMETYPE 		+ " =? and " +
//						Organization.TYPE 	+ " =? ",
//						new String[]{
//								contactId,
//								Organization.CONTENT_ITEM_TYPE,
//								Organization.TYPE_WORK + ""})
//								.withYieldAllowed(true);
//				if (company != null) {
//					tempBuilder.withValue(Organization.COMPANY, company);
//				}
//				if (job != null) {
//					tempBuilder.withValue(Organization.TITLE, job);
//				}
//				if (department != null) {
//					tempBuilder.withValue(Organization.DEPARTMENT, department);
//				}
//				ops.add(tempBuilder.build());
//			}
//			//更新电话号码
//			if (!Utils.isEmpty(phones)) {
//				for (String key : phones.keySet()) {
//					if (Utils.isEmpty(key)) {//不能为空
//						continue;
//					}
//					String[] temp = key.split("_");
//					if (!Utils.isNumber(temp[1])) {
//						continue;
//					}
//					Long dataId = Long.parseLong(temp[1]);
////					String phoneType = temp[1];//电话的类型，不需要，因为dataid是唯一的所以根据id修改数据就行了
//
//					String where = Data._ID + " =?";
////					   + " and " + Data.RAW_CONTACT_ID + " =? and " + Data.MIMETYPE + " =?";
//
//					String[] val = new String[] {String.valueOf(dataId),};
//					//String.valueOf(contactId), Phone.CONTENT_ITEM_TYPE
//
//					//如果电话号码为空，就删除这个电话号码，如果不为空，更新
//					if (Utils.isEmpty(phones.get(key))) {
//						op = ContentProviderOperation.newDelete(uri)
//								.withSelection(where, val)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					} else {
//						op = ContentProviderOperation.newUpdate(uri)
//								.withSelection(where,  val)
//								.withValue(Phone.NUMBER, phones.get(key).toString())
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					}
//				}
//			}
//			//更新电子邮件
//			if (!Utils.isEmpty(emails)) {
//				for (String key : emails.keySet()) {
//					if (Utils.isEmpty(key)) {//不能为空
//						continue;
//					}
//					String[] temp = key.split("_");
//					if (!Utils.isNumber(temp[1])) {
//						continue;
//					}
//					Long dataId = Long.parseLong(temp[1]);
////					String phoneType = temp[1];//Email的类型，不需要，因为dataid是唯一的所以根据id修改数据就行了
//
//					String where = Data._ID + " =?";
//
//					String[] val = new String[] {String.valueOf(dataId),};
//
//					//如果Email为空，就删除这个Email，如果不为空，更新
//					if (Utils.isEmpty(emails.get(key))) {
//						op = ContentProviderOperation.newDelete(uri)
//								.withSelection(where, val)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					} else {
//						op = ContentProviderOperation.newUpdate(uri)
//								.withSelection(where,  val)
//								.withValue(Email.DATA, emails.get(key).toString())
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					}
//				}
//			}
//			//更新地址
//			if (!Utils.isEmpty(address)) {
//				for (String key : address.keySet()) {
//					if (Utils.isEmpty(key)) {//不能为空
//						continue;
//					}
//					String[] temp = key.split("_");
//					if (!Utils.isNumber(temp[1])) {
//						continue;
//					}
//					Long dataId = Long.parseLong(temp[1]);
////					String phoneType = temp[1];//地址的类型，不需要，因为dataid是唯一的所以根据id修改数据就行了
//
//					String where = Data._ID + " =?";
//
//					String[] val = new String[] {String.valueOf(dataId),};
//
//					//如果地址为空，就删除这个地址，如果不为空，更新
//					if (Utils.isEmpty(address.get(key))) {
//						op = ContentProviderOperation.newDelete(uri)
//								.withSelection(where, val)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					} else {
//						op = ContentProviderOperation.newUpdate(uri)
//								.withSelection(where,  val)
//								.withValue(StructuredPostal.FORMATTED_ADDRESS, address.get(key).toString())
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					}
//				}
//			}
//
//			//更新im
//			if (!Utils.isEmpty(ims)) {
//				for (String key : ims.keySet()) {
//					if (Utils.isEmpty(key)) {//不能为空
//						continue;
//					}
//					String[] temp = key.split("_");
//					if (!Utils.isNumber(temp[1])) {
//						continue;
//					}
//					Long dataId = Long.parseLong(temp[1]);
//
//					String where = Data._ID + " =?";
//
//					String[] val = new String[] {String.valueOf(dataId),};
//
//					//如果im为空，就删除这个im，如果不为空，更新
//					if (Utils.isEmpty(ims.get(key))) {
//						op = ContentProviderOperation.newDelete(uri)
//								.withSelection(where, val)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					} else {
//						op = ContentProviderOperation.newUpdate(uri)
//								.withSelection(where,  val)
//								.withValue(Im.DATA, ims.get(key).toString())
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					}
//				}
//			}
//			//更新 urls
//			if (!Utils.isEmpty(urls)) {
//				for (String key : urls.keySet()) {
//					if (Utils.isEmpty(key)) {//不能为空
//						continue;
//					}
//					String[] temp = key.split("_");
//					if (!Utils.isNumber(temp[1])) {
//						continue;
//					}
//					Long dataId = Long.parseLong(temp[1]);
//
//					String where = Data._ID + " =?";
//
//					String[] val = new String[] {String.valueOf(dataId),};
//
//					//如果im为空，就删除这个im，如果不为空，更新
//					if (Utils.isEmpty(urls.get(key))) {
//						op = ContentProviderOperation.newDelete(uri)
//								.withSelection(where, val)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					} else {
//						op = ContentProviderOperation.newUpdate(uri)
//								.withSelection(where,  val)
//								.withValue(Website.URL, urls.get(key).toString())
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					}
//				}
//			}
//			//更新组
//			if (!Utils.isEmpty(groups)) {
//				for (String key : groups.keySet()) {
//					if (Utils.isEmpty(key)) {//不能为空
//						continue;
//					}
//					//1:title
//					String[] temp = key.split("_");
//					if (!Utils.isNumber(temp[2]) && !isExistGroup(temp[2])) {
//						continue;
//					}
//					Long dataId = Long.parseLong(temp[1]);
//					Long groupId = Long.parseLong(temp[2]);
//
//					String where = Data._ID + " =?";
//
//					String[] val = new String[] {String.valueOf(dataId),};
//					//如果组的title为空，就删除和这个组的关系，如果不为空，更新
//					if (Utils.isEmpty(groups.get(key))) {
//						op = ContentProviderOperation.newDelete(uri)
//								.withSelection(where, val)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					} else {
//						op = ContentProviderOperation.newUpdate(uri)
//								.withSelection(where,  val)
//								.withValue(GroupMembership.GROUP_ROW_ID, groupId)
//								.withYieldAllowed(true)
//								.build();
//						ops.add(op);
//					}
//				}
//			}
//			try {
//				ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, ops);
//				for (ContentProviderResult result : results) {
//					num += result.count;
//					Log.i(Const.APPTAG, "更新影响的行数：" + result.count);
//				}
//				updateResult.put("result", "1");
//				updateResult.put("obj", num);
//			} catch (Exception e) {
//				Log.i(Const.APPTAG, e.getMessage());
//				updateResult.put("result", "-1");
//				updateResult.put("obj",  "更新失败:" + e.getMessage());
//			}
//		}
//		if (updateResult.size() == 0) {
//			updateResult.put("result", "0");
//			updateResult.put("obj", "执行了更新，但是没有受影响的数据，请检查数据是否有效！");
//		}
//		return updateResult;
//	}
//
//	/**
//	 * 给联系人添加一个或多个电话号码
//	 * @param phones
//	 * @return
//	 */
//	public HashMap<String, Object> addPhoneByContactId(Map<String, String> phones) {
//		HashMap<String, Object> addResult = new HashMap<String, Object>();
//		if (Utils.isEmpty(phones)) {
//			addResult.put("result", "0");
//			addResult.put("obj", "无效插入，电话为空！");
//			return addResult;
//		}
//
//		Uri uri = Data.CONTENT_URI;
//		//插入电话号码
//		String id = phones.get("contactId");
//		String phone = phones.get("phone");
//		String type = phones.get("type");
//		//联系人id和电话都不能为空
//		if (Utils.isNumber(id) && !Utils.isEmpty(phone)) {
//			ContentValues values = new ContentValues();
//			values.put(Data.RAW_CONTACT_ID, id);
//			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
//			values.put(Phone.NUMBER, phone);
//			values.put(Phone.LABEL, "手机号");
//
//			if(type.equalsIgnoreCase("mobile")) {//如果是手机号码
//				values.put(Phone.TYPE, Phone.TYPE_MOBILE);
//			} else if(type.equalsIgnoreCase("work")) {//如果是公司电话
//				values.put(Phone.TYPE, Phone.TYPE_WORK);
//			} else if(type.equalsIgnoreCase("faxWork")) {//如果是公司传真号码
//				values.put(Phone.TYPE, Phone.TYPE_FAX_WORK);
//			} else if(type.equalsIgnoreCase("home")) {//如果是家庭电话
//				values.put(Phone.TYPE, Phone.TYPE_HOME);
//			} else {//其他
//				values.put(Phone.TYPE, Phone.TYPE_OTHER);
//			}
//			Uri u = resolver.insert(uri, values);
//
//			addResult.put("result", "1");
//			addResult.put("obj", u.toString());
//		} else {
//			addResult.put("result", "0");
//			addResult.put("obj", "无效插入，联系人id和电话为空！");
//		}
//
//		return addResult;
//	}
////	/**
////	 * 删除和组的所有关系
////	 */
////	private void test(){
//		//不管分组的数据是否为空,都先删除联系人和组的所有关系，然后再添加，新的关系
////		String where = GroupMembership.RAW_CONTACT_ID + "=? and " +
////				GroupMembership.MIMETYPE + "=?";
////		String[] val = { contactId, GroupMembership.CONTENT_ITEM_TYPE};
////
////		op = ContentProviderOperation.newDelete(uri)
////				.withSelection(where, val)
////				.withYieldAllowed(true)
////				.build();
////		ops.add(op);
////	}
//}