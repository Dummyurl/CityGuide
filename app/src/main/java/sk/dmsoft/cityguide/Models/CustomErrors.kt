package sk.dmsoft.cityguide.Models

class CustomErrors {
    var errors: ArrayList<CustomError> = ArrayList()
}

class CustomError{
    var code = ""
    var description = ""
    var field = ""
}