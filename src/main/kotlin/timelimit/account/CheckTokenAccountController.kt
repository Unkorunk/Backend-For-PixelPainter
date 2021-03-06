package timelimit.account

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class CheckTokenAccountController {
    class CheckToken(val status : String)

    @RequestMapping("/account/check_token")
    fun checkToken(@RequestParam("token") token : String) : CheckToken {
        if (token.length != 32) {
            return CheckToken("INVALID_TOKEN")
        }

        var status = "FAIL"
        transaction {
            val query = Users.select { Users.token eq token }
            if (query.count() != 1) {
                status = "INVALID_TOKEN"
                return@transaction
            }
            val user = query.iterator().next()
            val tokenTime = user[Users.token_time] ?: return@transaction
            if (tokenTime < DateTime.now()) {
                status = "INVALID_TOKEN"
                return@transaction
            }
            status = "OK"
        }
        return CheckToken(status)
    }
}