package com.github.guch8017.db2021;

public class Static {
    public final static String HOST = "http://172.20.10.9:5000";
    public final static String LOGIN = HOST + "/manage/login";
    public final static String LOGOUT = HOST + "/manage/logout";

    public final static String CUSTOMER_SEARCH = HOST + "/customer/query";
    public final static String CUSTOMER_CREATE = HOST + "/customer/create";
    public final static String CUSTOMER_MODIFY = HOST + "/customer/update";
    public final static String CUSTOMER_DELETE = HOST + "/customer/delete";
    public final static String CUSTOMER_GET = HOST + "/customer/get_all";

    public final static String BRANCH_GET = HOST + "/branch/get_all";

    public final static String ACCOUNT_SEARCH = HOST + "/account/query";
    public final static String ACCOUNT_CREATE = HOST + "/account/create";
    public final static String ACCOUNT_MODIFY = HOST + "/account/modify";
    public final static String ACCOUNT_DELETE = HOST + "/account/delete";
    public final static String ACCOUNT_GET = HOST + "/account/get_all";

    public final static String LOAN_SEARCH = HOST + "/loan/query";
    public final static String LOAN_CREATE = HOST + "/loan/create";
    public final static String LOAN_DELETE = HOST + "/loan/delete";
    public final static String LOAN_PAY = HOST + "/loan/pay";
    public final static String LOAN_GET = HOST + "/loan/get_all";

    public final static String STAT_GET = HOST + "/stat/query_user";
    public final static String STAT_GET_B = HOST + "/stat/query_branch";

    public final static String JSON_MEDIA = "application/json; charset=utf-8";

    public final static int METHOD_ID = 0;
    public final static int METHOD_NAME = 1;
    public final static int METHOD_PHONE = 2;
    public final static int METHOD_BRANCH = 1;

    public final static int ERR_NO_LOGIN = 403;

    public final static String HEADER_USER = "USER_ID";
    public final static String HEADER_SESS = "SESSION";

    public final static int ACCOUNT_TYPE_SAVING = 0;
    public final static int ACCOUNT_TYPE_CHEQUES = 1;
}
