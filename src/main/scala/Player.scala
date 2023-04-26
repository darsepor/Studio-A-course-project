package base

abstract class Player(){
    var currency = 100
    
}

case class Human(name:String) extends Player(){
    
}

case class AI(name:String) extends Player(){
     

}