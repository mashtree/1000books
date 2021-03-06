package uxt6.psu.com.a1000books.utility;

/**
 * Created by aisyahumar on 3/4/2018.
 */

public class EndPoints {
    //public static final String ROOT_URL = "http://192.168.56.1/apibook/";
    public static final String ROOT_URL = "https://apibook.000webhostapp.com/";
    private static final String READER_ROOT_URL = ROOT_URL+"api.php?apireader=";
    private static final String BOOK_ROOT_URL = ROOT_URL+"api.php?apibook=";
    private static final String COMMENT_ROOT_URL = ROOT_URL+"api.php?apicomment=";
    //private static final String READER_ROOT_URL = "https://apibook.000webhostapp.com/api.php?apireader=";
    //private static final String BOOK_ROOT_URL = "https://apibook.000webhostapp.com/api.php?apibook=";
    //private static final String COMMENT_ROOT_URL = "https://apibook.000webhostapp.com/api.php?apicomment=";
    public static final String POST_DO_LOGIN = READER_ROOT_URL + "login";
    public static final String POST_READER_URL = READER_ROOT_URL + "postreader";
    public static final String GET_READER_URL = READER_ROOT_URL + "getreader";
    public static final String GET_READER_BY_BOOK_URL = READER_ROOT_URL + "getreaderbybook&id=";
    public static final String EDIT_READER_URL = READER_ROOT_URL + "editreader";
    public static final String POST_BOOK_URL = BOOK_ROOT_URL + "postbook";
    public static final String SEARCH_BOOK_URL = BOOK_ROOT_URL + "search&keyword=";
    public static final String GET_BOOK_URL = BOOK_ROOT_URL + "each&id=";
    public static final String POST_BOOK_GPLUS = BOOK_ROOT_URL + "gplus";
    public static final String POST_COMMENT_URL = COMMENT_ROOT_URL + "postcomment";



}
