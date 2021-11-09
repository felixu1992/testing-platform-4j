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
        String USER = "'USER-'";
        String CHILD_USER = "'CHILD-USER-'";
    }

    interface ContactorGroup {
        String NAME = "CONTACTOR-GROUP";
        String CONTACTOR_GROUP = "'CONTACTOR-GROUP-'";
        String CONTACTOR_GROUP_LIST = "'CONTACTOR-GROUP-LIST-'";
    }

    interface Contactor {
        String NAME = "CONTACTOR";
        String CONTACTOR = "'CONTACTOR-'";
    }

    interface FileGroup {
        String NAME = "FILE-GROUP";
        String FILE_GROUP = "'FILE-GROUP-'";
        String FILE_GROUP_LIST = "'FILE-GROUP-LIST-'";
    }

    interface File {
        String NAME = "FILE";
        String FILE = "'FILE-'";
    }

    interface ProjectGroup {
        String NAME = "PROJECT-GROUP";
        String PROJECT_GROUP = "'PROJECT-GROUP-'";
        String PROJECT_GROUP_LIST = "'PROJECT-GROUP-LIST-'";
    }

    interface Project {
        String NAME = "PROJECT";
        String PROJECT = "'PROJECT-'";
    }

    interface UserProject {
        String NAME = "USER-PROJECT";
        String RELATION = "'USER-PROJECT-'";
    }

    interface CaseInfoGroup {
        String NAME = "CASE-GROUP";
        String CASE_GROUP = "'CASE-GROUP-'";
        String DEFAULT_GROUP = "'DEFAULT-CASE-GROUP-'";
        String CASE_GROUP_LIST = "'CASE-GROUP-LIST-'";
    }

    interface CaseInfo {
        String NAME = "CASE";
        String CASE = "'CASE-'";
        String PROJECT_GROUP_CASE_MAP = "'PROJECT-GROUP-CASE-MAP-'";
        String PROJECT_CASE_LIST = "PROJECT-CASE-";
    }
}
