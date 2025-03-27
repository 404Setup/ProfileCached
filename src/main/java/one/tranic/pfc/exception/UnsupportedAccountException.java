package one.tranic.pfc.exception;

public class UnsupportedAccountException extends RuntimeException {
    public UnsupportedAccountException() {
        super("ProfileCached is not compatible with non-OnlineMode accounts! (i.e. accounts that do not perform account verification, or accounts that use the third-party Yggdrasil verification server)");
    }
}
