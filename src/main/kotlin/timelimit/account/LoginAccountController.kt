package timelimit.account

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.sin
import kotlin.random.Random

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class LoginAccountController {
    class Login constructor(val status: String, val token: String)

    @RequestMapping("/account/login")
    public fun login(@RequestParam(value="login") login: String, @RequestParam(value="password") raw_password: String) : Login {
        login.forEach {
            if (it !in 'a'..'z' && it !in 'A'..'Z' && it !in '0'..'9') {
                return Login("FAIL", "")
            }
        }

        if (raw_password.length < 6 || raw_password.length > 128 || login.length < 6 || login.length > 32) {
            return Login("FAIL", "")
        }
        var password = raw_password
        password = (sin(password.length / 32.0) * 100.0).toString() + password + "xGhw663rTh12"
        val md = MessageDigest.getInstance("MD5").digest(password.toByteArray())
        password = BigInteger(1, md).toString(16).padStart(32, '0')

        var status = false
        var token = ""
        transaction {
            val user = Users.select {(Users.login eq login) and (Users.password eq password)}.limit(1).toList()
            if (user.size == 1) {
                token = login + user[0][Users.user_id] + password + Random.nextInt()
                status = true
            }
        }

        if (!status) {
            return Login("FAIL", "")
        } else {
            val md = MessageDigest.getInstance("MD5").digest(token.toByteArray())
            token = BigInteger(1, md).toString(16).padStart(32, '0')

            status = false
            transaction {
                if (Users.select{Users.token eq token}.count() == 0) {
                    Users.update(where = {Users.login eq login}) {
                        it[Users.token] = token
                        it[Users.token_time] = DateTime.now().plusDays(1)
                    }
                    status = true
                } else {
                    // TODO: Generate other token
                }
            }

            return if (status) {
                Login("OK", token)
            } else {
                Login("FAIL", "")
            }
        }
    }
}