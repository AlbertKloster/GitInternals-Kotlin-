package gitinternals

import java.util.zip.InflaterInputStream

data class ObjectTypeInflater(val objectType: ObjectType?, val inflater: InflaterInputStream)