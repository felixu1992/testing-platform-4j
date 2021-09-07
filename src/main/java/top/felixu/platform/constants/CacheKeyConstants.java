package top.felixu.platform.constants;

/**
 * Redis 缓存相关的常量
 *
 * @author felixu
 * @since 2021.08.24
 */
public interface CacheKeyConstants {

    interface Token {
        String PREFIX = "token-";
    }

    interface User {
        String NAME = "USER";
        String USER_CACHE = "'USER-CACHE-'";
        String CHILD_USER_CACHE = "'CHILD-USER-CACHE-'";
    }

    interface ContactorGroup {
        String NAME = "CONTACTOR-GROUP";
        String CONTACTOR_GROUP_CACHE = "'CONTACTOR-GROUP-CACHE-'";
        String CONTACTOR_GROUP_LIST_CACHE = "'CONTACTOR-GROUP-LIST-CACHE-'";
    }

    interface Contactor {
        String NAME = "CONTACTOR";
        String CONTACTOR_CACHE = "'CONTACTOR-CACHE-'";
    }

    interface FileGroup {
        String NAME = "FILE-GROUP";
        String FILE_GROUP_CACHE = "'FILE-GROUP-CACHE-'";
        String FILE_GROUP_LIST_CACHE = "'FILE-GROUP-LIST-CACHE-'";
    }

    interface File {
        String NAME = "FILE";
        String FILE_CACHE = "'FILE-CACHE-'";
    }

    interface ProjectGroup {
        String NAME = "PROJECT-GROUP";
        String PROJECT_GROUP_CACHE = "'PROJECT-GROUP-CACHE-'";
        String PROJECT_GROUP_LIST_CACHE = "'PROJECT-GROUP-LIST-CACHE-'";
    }

    interface Project {
        String NAME = "PROJECT";
        String PROJECT_CACHE = "'PROJECT-CACHE-'";
    }

    interface CaseInfoGroup {
        String NAME = "CASE-GROUP";
        String CASE_GROUP_CACHE = "'CASE-GROUP-CACHE-'";
        String CASE_GROUP_LIST_CACHE = "'CASE-GROUP-LIST-CACHE-'";
    }
}
