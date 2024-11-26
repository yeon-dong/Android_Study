import java.util.Scanner

fun main() {
    // 입력을 받기
    val scanner = Scanner(System.`in`)
    val n = scanner.nextInt()
    val string = scanner.next()

    // 1 : camel, 2 : snake, 3 : pascal
    when (n) {
        1 -> {
            println(string)
            for (ch: Char in string){
                if(ch in 'A'..'Z') {
                    print("_${ch.lowercaseChar()}")
                } else {
                    print("$ch")
                }
            }
            println()
            println(string.replaceFirstChar { c -> c.uppercaseChar() }) // 어차피 맨앞에만 키우면 되니까
        }
        2 -> {
            var flag = false
            var camel = ""
            for(ch : Char in string) {
                if(ch == '_') {
                    flag = true
                } else if(flag) {
                    camel += ch.uppercaseChar()
                    flag = false
                } else {
                    camel += ch
                }
            }
            println(camel)
            println(string)
            println(camel.replaceFirstChar { c -> c.uppercaseChar() })
        }
        3 -> {
            val camel = string.replaceFirstChar { c -> c.lowercaseChar() }
            println(camel)
            for (ch: Char in camel) {
                if(ch in 'A'..'Z') {
                    print("_${ch.lowercaseChar()}")
                } else {
                    print("$ch")
                }
            }
            println()
            println(string)
        }
    }
}
