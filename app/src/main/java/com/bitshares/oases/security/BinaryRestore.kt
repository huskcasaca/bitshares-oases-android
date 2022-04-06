package com.bitshares.oases.security

import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.PrivateKey
import bitshareskit.models.PublicKey
import bitshareskit.objects.GrapheneSerializable
import bitshareskit.objects.JsonSerializable
import graphene.extension.*
import compression.lzma.Lzma
import modulon.extensions.charset.toHexByteArray
import modulon.extensions.charset.toHexString
import modulon.extensions.charset.toUnicodeByteArray
import modulon.extensions.charset.toUnicodeString
import org.java_json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*



data class BinaryRestore(
    val accounts: List<BackupUser>,
    val privateKeys: Set<PrivateKey>,
//    val passwordPublicKey: PublicKey = PublicKey.EMPTY_KEY,
//    val encryptionKey: String = EMPTY_SPACE,
//    val encryptedBrainkey: String = EMPTY_SPACE,
//    val brainKeyPublicKey: PublicKey = PublicKey.EMPTY_KEY,
//    val brainKeySequence: Int = 0,

    val isCanonical: Boolean,
    val chainId: String = ChainConfig.Chain.CHAIN_ID_MAIN_NET,
    val created: Date = Date(),
    val lastModified: Date = Date(),
) : GrapheneSerializable {

    /*
    {
        "linked_accounts": [{
            "chainId": "4018d7844c78f6a6c41c6a552b898022310fc5dec06da467ee7905a8dad512c8",
            "uid": 129932,
            "name": "test-80"
        }, {
            "chainId": "4018d7844c78f6a6c41c6a552b898022310fc5dec06da467ee7905a8dad512c8",
            "name": "test-70"
        }],
        "private_keys": [{
            "id": 1,
            "encrypted_key": "f16cef269725ea79d518eb0e0b7adb8cd20a5beadc4837684ddd0d0027a8501c44a7a92b809c147487328614aa5d2d2e",
            "pubkey": "BTS7BGtaj4gb6W3rrjvkJhYearh2Me8ZbJQRW7K9ukdDttmMSKu1o"
        }, {
            "id": 2,
            "encrypted_key": "e6fe38d5984895e431d4be3f37bf551f5dd0576a038d49866710b112a9c2d5d0de7b03ca3da6f6e10c7a4ed18bc6bf41",
            "pubkey": "BTS6ZAFm3sF4QdSEbiYSzHduABZ3MwXdGVgPPK222yiyrCKKDRYvV"
        }, {
            "id": 3,
            "encrypted_key": "80a985d8564aa882c9d01e23603bd4ed48788b2471de8c278536cd8397ceca4443218f0ae66d2d8e7672074b99e35f1c",
            "pubkey": "BTS6bZn8JXDpwkCYD6MqK3jF39wBfHcrxd7rihY2Bva32vopBExSN"
        }, {
            "id": 4,
            "encrypted_key": "e6c47e5a307c50cac0c69c782e3a48bd22c5b716087a7508f7dde78cda19a402f7b0892de8e07cf4186b792cfe9ddb2d",
            "pubkey": "BTS8WXVDH3smwXHqt5TN6Lh9JGnhqBDSPM9ZHuNK9Lrv2kmJDoMNo"
        }],
        "wallet": [{
            "public_name": "default",
            "created": "2020-08-06T06:22:48.000Z",
            "last_modified": "2020-08-06T06:35:54.000Z",
            "password_pubkey": "BTS53GNAmkD4oWtM3DqbfF8oKKMNwb6wZWaCQnm2RQQWfdxzGJyG1",
            "encryption_key": "5145591f3105885eea0b641b065c4b0ce872eff8ac2c87d2fdc90b1835699531a2fe71e6fc3d75532285c63f142caa0c",
            "encrypted_brainkey": "ca7d9d562f47a5003362ae194a3580db14112ce8d8f9bad64bcc03f55d925b9061a92f73fd8927d703b57d78bc2b8098be7c394339df70dc901611ef9895110c650e764f8628611def4c8797d4d2101678be81b0c6654dbfe479461124885966915b0a5219359101b3dcc20dc2256aac8046def064ce3cd036e5a842b3f38983",
            "brainkey_pubkey": "BTS7zFUgZFVYHWC4czRXXLaJpB11Lwp4oDCHXdyiqQHS35Z1WMBLr",
            "brainkey_sequence": 0,
            "chain_id": "4018d7844c78f6a6c41c6a552b898022310fc5dec06da467ee7905a8dad512c8",
            "author": "BTS++"
        }]
    }
     */

    data class BackupUser(
        val uid: Long,
        val name: String,
        val chainId: String,
        val owner: Set<PublicKey> = emptySet(),
        val active: Set<PublicKey> = emptySet(),
        val memo: Set<PublicKey> = emptySet()
    ) : JsonSerializable {

        companion object {

            const val KEY_ACCOUNT_UID = "uid"
            const val KEY_ACCOUNT_NAME = "name"
            const val KEY_ACCOUNT_CHAIN_ID = "chainId"
            const val KEY_OWNER_KEYS = "owner_keys"
            const val KEY_ACTIVE_KEYS = "active_keys"
            const val KEY_MEMO_KEYS = "memo_keys"

            fun fromJson(rawJson: JSONObject): BackupUser {
                return BackupUser(
                    rawJson.optLong(KEY_ACCOUNT_UID, ChainConfig.EMPTY_INSTANCE),
                    rawJson.optString(KEY_ACCOUNT_NAME),
                    rawJson.optString(KEY_ACCOUNT_CHAIN_ID),
                    rawJson.optIterable<String>(KEY_OWNER_KEYS).map { PublicKey.fromAddress(it) }.toSet(),
                    rawJson.optIterable<String>(KEY_ACTIVE_KEYS).map { PublicKey.fromAddress(it) }.toSet(),
                    rawJson.optIterable<String>(KEY_MEMO_KEYS).map { PublicKey.fromAddress(it) }.toSet(),
                )
            }
        }

        override fun toJsonElement(): JSONObject = buildJsonObject {
            putItem(KEY_ACCOUNT_CHAIN_ID, chainId)
            putItem(KEY_ACCOUNT_NAME, name)
            putItem(KEY_ACCOUNT_UID, uid)
            putArraySerializable(KEY_OWNER_KEYS, owner)
            putArraySerializable(KEY_ACTIVE_KEYS, active)
            putArraySerializable(KEY_MEMO_KEYS, memo)
        }

    }

    companion object {
        const val KEY_LINKED_ACCOUNTS = "linked_accounts"
        const val KEY_PRIVATE_KEYS = "private_keys"
        const val KEY_ID = "id"
        const val KEY_ENCRYPTED_KEY = "encrypted_key"
        const val KEY_PUBLIC_KEY = "pubkey"

        const val KEY_WALLET = "wallet"
        const val KEY_PUBLIC_NAME = "public_name"
        const val KEY_PASSWORD_PUBKEY = "password_pubkey"
        const val KEY_ENCRYPTION_KEY = "encryption_key"
        const val KEY_ENCRYPTED_BRAINKEY = "encrypted_brainkey"
        const val KEY_BRAINKEY_PUBKEY = "brainkey_pubkey"
        const val KEY_BRAINKEY_SEQUENCE = "brainkey_sequence"

        const val KEY_CREATED = "created"
        const val KEY_LAST_MODIFIED = "last_modified"
        const val KEY_BRAINKEY_BACKUP_DATE = "brainkey_backup_date"
        const val KEY_CHAIN_ID = "chain_id"
        const val KEY_IDENTIFIER = "identifier"

        // TODO: 2022/2/19 rename
        const val IDENTIFIER_APPLICATION = "android"

        fun fromFile(bin: ByteArray, passphrase: String): BinaryRestore {
            if (bin.size <= 46) throw InvalidFileStreamException("Invalid file size [${bin.size}]")
            val passPrivKey = PrivateKey.fromSeed(passphrase, PublicKey.BITSHARES_MAINNET_PREFIX)
            val passPubKey = passPrivKey.publicKey ?: throw IncorrectPasswordException("Incorrect passphrase seed")
            val randomPubKeyBytes = bin.copyOfRange(0, 33)
            val encrypted = bin.copyOfRange(33, bin.size)
            val randomPubKey = PublicKey.fromKeyBytes(randomPubKeyBytes, PublicKey.BITSHARES_MAINNET_PREFIX)
            if (randomPubKey.isInvalid) throw InvalidFileStreamException("Invalid file header")
            val decrypted = runCatching {
                aesDecryptWithChecksum(passPrivKey, randomPubKey, encrypted)
            }.getOrElse { it.printStackTrace(); throw IncorrectPasswordException("Incorrect passphrase seed") }
            val uncompressed = ByteArrayOutputStream()
            Lzma.decode(ByteArrayInputStream(decrypted), uncompressed)
            return runCatching {
                val rawJson = JSONObject(uncompressed.toByteArray().toUnicodeString())
                val walletJson = rawJson.getJSONArray(KEY_WALLET).getJSONObject(0)
                require(passPubKey.address == walletJson.optString(KEY_PASSWORD_PUBKEY))
                val masterSeedEncrypted = walletJson.optString(KEY_ENCRYPTION_KEY).toHexByteArray()
                val masterSeed = aesDecrypt(passphrase.toUnicodeByteArray(), masterSeedEncrypted)
                BinaryRestore(
                    rawJson.optIterable<JSONObject>(KEY_LINKED_ACCOUNTS).map { BackupUser.fromJson(it) }.filter { it.name.isNotEmpty() },
                    rawJson.optIterable<JSONObject>(KEY_PRIVATE_KEYS).map { PrivateKey(it.optString(KEY_ENCRYPTED_KEY).toHexByteArray(), PrivateKey.KeyType.RESTORE).apply { decrypt(masterSeed) } }.toSet(),
                    walletJson.optString(KEY_IDENTIFIER) == IDENTIFIER_APPLICATION,
                    walletJson.optString(KEY_CHAIN_ID, ChainConfig.Chain.CHAIN_ID_MAIN_NET),
                    walletJson.optGrapheneTime(KEY_CREATED),
                    walletJson.optGrapheneTime(KEY_LAST_MODIFIED)
                )
            }.getOrElse { it.printStackTrace(); throw InvalidFileStreamException("Invalid file content") }
        }
    }

    class InvalidFileStreamException(override val message: String?) : Exception(message)
    class IncorrectPasswordException(override val message: String?) : Exception(message)

    lateinit var json: JSONObject

    var encoded = byteArrayOf()

    @Synchronized
    fun encodeToBinary(passphrase: String): ByteArray {
        val passPrivKey = PrivateKey.fromSeed(passphrase, PublicKey.BITSHARES_MAINNET_PREFIX)
        val passPubKey = passPrivKey.publicKey
        val randomPrivKey = PrivateKey.random(PublicKey.BITSHARES_MAINNET_PREFIX)
        val randomPubKey = randomPrivKey.publicKey

        val masterSeed = nextSecureRandomBytes(32)
        val masterSeedEncrypted = aesEncrypt(passphrase.toUnicodeByteArray(), masterSeed) // encryption_key

        val keys = privateKeys.onEach { it.apply { encrypt(masterSeed) } }.filter { it.isValid }

        val rawJson = buildJsonObject {
            putArraySerializable(KEY_LINKED_ACCOUNTS, accounts)
            putList(KEY_PRIVATE_KEYS, keys.mapIndexed { index, key ->
                buildJsonObject {
                    putItem(KEY_ID, index + 1)
                    putItem(KEY_ENCRYPTED_KEY, key.keyBytes!!.toHexString())
                    putItem(KEY_PUBLIC_KEY, key.publicKey.address)
                }
            })
            putList(
                KEY_WALLET, listOf(
                    buildJsonObject {
                        putItem(KEY_PUBLIC_NAME, "default")
                        putItem(KEY_CREATED, created.toInstant().toString())
                        putItem(KEY_LAST_MODIFIED, lastModified.toInstant().toString())
                        putItem(KEY_PASSWORD_PUBKEY, passPubKey.address)
                        putItem(KEY_ENCRYPTION_KEY, masterSeedEncrypted.toHexString())
                        putItem(KEY_CHAIN_ID, chainId)
                        putItem(KEY_IDENTIFIER, IDENTIFIER_APPLICATION)
                    }
                ))
        }
        json = rawJson

        val bytesJson = rawJson.toString().toUnicodeByteArray()

        val compressed = ByteArrayOutputStream(bytesJson.size + 256)
        Lzma.encode(ByteArrayInputStream(bytesJson), compressed, bytesJson.size)

        val encrypted = aesEncryptWithChecksum(randomPrivKey, passPubKey, compressed.toByteArray())

        encoded = randomPubKey.keyBytes!! + encrypted
        return encoded
    }

    override fun toJsonElement(): JSONObject {
        return JSONObject(
            """""".trimIndent()
        )
    }

    override fun toByteArray(): ByteArray {
        val bytesJson = toJsonElement().toString().toUnicodeByteArray()
        val compressed = ByteArrayOutputStream(bytesJson.size + 256)
        Lzma.encode(ByteArrayInputStream(bytesJson), compressed, bytesJson.size)
        return ByteArray(0)
    }


}