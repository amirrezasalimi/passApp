package ir.amirsalimi.passapp.app

class AppConfig {
    var VERSION = "0.1"

    companion object {
        val APP_KEY = "fdsgd$%#$^fdgf"

        private val API_KEY = "1"
        var HOST = "http://192.168.1.102/"
        var API_Address =
            HOST + "api/" + AppConfig.API_KEY + "/"
        var DATABASE = "db"
    }
}
