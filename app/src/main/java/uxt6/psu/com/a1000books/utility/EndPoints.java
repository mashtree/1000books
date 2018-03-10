package uxt6.psu.com.a1000books.utility;

/**
 * Created by aisyahumar on 3/4/2018.
 */

public class EndPoints {
    private static final String READER_ROOT_URL = "http://192.168.56.1/apibook/api.php?apireader=";
    private static final String BOOK_ROOT_URL = "http://192.168.56.1/apibook/api.php?apibook=";
    private static final String COMMENT_ROOT_URL = "http://192.168.56.1/apibook/api.php?apicomment=";
    public static final String ROOT_URL = "http://192.168.56.1/apibook/";
    public static final String POST_READER_URL = READER_ROOT_URL + "postreader";
    public static final String GET_READER_URL = READER_ROOT_URL + "getreader";
    public static final String EDIT_READER_URL = READER_ROOT_URL + "editreader";
    public static final String POST_BOOK_URL = BOOK_ROOT_URL + "postbook";
    public static final String SEARCH_BOOK_URL = BOOK_ROOT_URL + "search&keyword=";
    public static final String GET_BOOK_URL = BOOK_ROOT_URL + "each&id=";
    public static final String POST_COMMENT_URL = COMMENT_ROOT_URL + "postcomment";
}
