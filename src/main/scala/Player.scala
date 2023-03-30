package base

abstract class Player(){
    var currency = 100
    def advance_turn():Unit
}

case class Human(name:String) extends Player(){
    def advance_turn():Unit = {

    }
}

case class AI(name:String) extends Player(){
     def advance_turn():Unit = {
        
     }


}