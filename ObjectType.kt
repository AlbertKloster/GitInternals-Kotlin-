package gitinternals

import java.lang.RuntimeException

enum class ObjectType(val string: String) {
    BLOB("blob"),
    COMMIT("commit"),
    ;

    companion object {
        fun getObjectType(objectTypeString: String): ObjectType {
            for (type in ObjectType.values()) {
                if (type.string == objectTypeString) return type
            }
            throw RuntimeException("Wrong Object Type: $objectTypeString")
        }
    }
}