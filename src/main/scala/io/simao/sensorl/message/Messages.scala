// Generated by ScalaBuff, the Scala Protocol Buffers compiler. DO NOT EDIT!
// source: messages.proto

package io.simao.sensorl.message

final case class Measurement (
	`mid`: Int = 0,
	`value`: Double = 0.0,
	`time`: String = "",
	`key`: String = ""
) extends com.google.protobuf.GeneratedMessageLite
	with com.google.protobuf.MessageLite.Builder
	with net.sandrogrzicic.scalabuff.Message[Measurement]
	with net.sandrogrzicic.scalabuff.Parser[Measurement] {



	def writeTo(output: com.google.protobuf.CodedOutputStream) {
		output.writeUInt32(1, `mid`)
		output.writeDouble(2, `value`)
		output.writeString(3, `time`)
		output.writeString(4, `key`)
	}

	def getSerializedSize = {
		import com.google.protobuf.CodedOutputStream._
		var __size = 0
		__size += computeUInt32Size(1, `mid`)
		__size += computeDoubleSize(2, `value`)
		__size += computeStringSize(3, `time`)
		__size += computeStringSize(4, `key`)

		__size
	}

	def mergeFrom(in: com.google.protobuf.CodedInputStream, extensionRegistry: com.google.protobuf.ExtensionRegistryLite): Measurement = {
		import com.google.protobuf.ExtensionRegistryLite.{getEmptyRegistry => _emptyRegistry}
		var __mid: Int = 0
		var __value: Double = 0.0
		var __time: String = ""
		var __key: String = ""

		def __newMerged = Measurement(
			__mid,
			__value,
			__time,
			__key
		)
		while (true) in.readTag match {
			case 0 => return __newMerged
			case 8 => __mid = in.readUInt32()
			case 17 => __value = in.readDouble()
			case 26 => __time = in.readString()
			case 34 => __key = in.readString()
			case default => if (!in.skipField(default)) return __newMerged
		}
		null
	}

	def mergeFrom(m: Measurement) = {
		Measurement(
			m.`mid`,
			m.`value`,
			m.`time`,
			m.`key`
		)
	}

	def getDefaultInstanceForType = Measurement.defaultInstance
	def clear = getDefaultInstanceForType
	def isInitialized = true
	def build = this
	def buildPartial = this
	def parsePartialFrom(cis: com.google.protobuf.CodedInputStream, er: com.google.protobuf.ExtensionRegistryLite) = mergeFrom(cis, er)
	override def getParserForType = this
	def newBuilderForType = getDefaultInstanceForType
	def toBuilder = this
	def toJson(indent: Int = 0): String = {
		val indent0 = "\n" + ("\t" * indent)
		val (indent1, indent2) = (indent0 + "\t", indent0 + "\t\t")
		val sb = StringBuilder.newBuilder
		sb
			.append("{")
			sb.append(indent1).append("\"mid\": ").append("\"").append(`mid`).append("\"").append(',')
			sb.append(indent1).append("\"value\": ").append("\"").append(`value`).append("\"").append(',')
			sb.append(indent1).append("\"time\": ").append("\"").append(`time`).append("\"").append(',')
			sb.append(indent1).append("\"key\": ").append("\"").append(`key`).append("\"").append(',')
		sb.length -= 1
		sb.append(indent0).append("}")
		sb.toString()
	}

}

object Measurement {
	@beans.BeanProperty val defaultInstance = new Measurement()

	def parseFrom(data: Array[Byte]): Measurement = defaultInstance.mergeFrom(data)
	def parseFrom(data: Array[Byte], offset: Int, length: Int): Measurement = defaultInstance.mergeFrom(data, offset, length)
	def parseFrom(byteString: com.google.protobuf.ByteString): Measurement = defaultInstance.mergeFrom(byteString)
	def parseFrom(stream: java.io.InputStream): Measurement = defaultInstance.mergeFrom(stream)
	def parseDelimitedFrom(stream: java.io.InputStream): Option[Measurement] = defaultInstance.mergeDelimitedFromStream(stream)

	val MID_FIELD_NUMBER = 1
	val VALUE_FIELD_NUMBER = 2
	val TIME_FIELD_NUMBER = 3
	val KEY_FIELD_NUMBER = 4

	def newBuilder = defaultInstance.newBuilderForType
	def newBuilder(prototype: Measurement) = defaultInstance.mergeFrom(prototype)

}

object Messages {
	def registerAllExtensions(registry: com.google.protobuf.ExtensionRegistryLite) {
	}

}
