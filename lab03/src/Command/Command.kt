package Command

abstract class Command {
    abstract fun execute()
    abstract fun undo()
}

class Elevator {
    private var currentFloor: Int = 1
    private var isDoorOpen: Boolean = false

    fun moveUp() {
        if (isDoorOpen) {
            println("Ошибка: невозможно двигаться с открытыми дверьми")
            return
        }
        currentFloor++
        println("Лифт поднялся на этаж $currentFloor")
    }

    fun moveDown() {
        if (isDoorOpen) {
            println("Ошибка: невозможно двигаться с открытыми дверьми")
            return
        }
        if (currentFloor > 1) {
            currentFloor--
            println("Лифт опустился на этаж $currentFloor")
        } else {
            println("Ошибка: лифт уже на первом этаже")
        }
    }

    fun openDoor() {
        isDoorOpen = true
        println("Двери лифта открыты на этаже $currentFloor")
    }

    fun closeDoor() {
        isDoorOpen = false
        println("Двери лифта закрыты")
    }

    fun getCurrentFloor(): Int = currentFloor
    fun isDoorOpen(): Boolean = isDoorOpen
}

class MoveUpCommand(private val elevator: Elevator) : Command() {
    private var previousFloor: Int = 0

    override fun execute() {
        previousFloor = elevator.getCurrentFloor()
        elevator.moveUp()
    }

    override fun undo() {
        if (previousFloor < elevator.getCurrentFloor()) {
            elevator.moveDown()
        }
    }
}

class MoveDownCommand(private val elevator: Elevator) : Command() {
    private var previousFloor: Int = 0

    override fun execute() {
        previousFloor = elevator.getCurrentFloor()
        elevator.moveDown()
    }

    override fun undo() {
        if (previousFloor > elevator.getCurrentFloor()) {
            elevator.moveUp()
        }
    }
}

class OpenDoorCommand(private val elevator: Elevator) : Command() {
    private var wasDoorOpen: Boolean = false

    override fun execute() {
        wasDoorOpen = elevator.isDoorOpen()
        elevator.openDoor()
    }

    override fun undo() {
        if (wasDoorOpen != elevator.isDoorOpen()) {
            elevator.closeDoor()
        }
    }
}

class CloseDoorCommand(private val elevator: Elevator) : Command() {
    private var wasDoorOpen: Boolean = false

    override fun execute() {
        wasDoorOpen = elevator.isDoorOpen()
        elevator.closeDoor()
    }

    override fun undo() {
        if (wasDoorOpen != elevator.isDoorOpen()) {
            elevator.openDoor()
        }
    }
}

class CommandHistory {
    private val history = mutableListOf<Command>()

    fun addCommand(command: Command) {
        history.add(command)
    }

    fun undoLast(): Boolean {
        if (history.isEmpty()) return false
        val lastCommand = history.removeAt(history.size - 1)
        lastCommand.undo()
        return true
    }

    fun undoLastN(n: Int): Int {
        var undoneCount = 0
        for (i in 0 until n) {
            if (undoLast()) {
                undoneCount++
            } else {
                break
            }
        }
        return undoneCount
    }

    fun clearHistory() {
        history.clear()
    }

    fun getHistorySize(): Int = history.size
}

class LiftControl {
    private val elevator = Elevator()
    private val history = CommandHistory()

    fun moveUp() {
        val command = MoveUpCommand(elevator)
        command.execute()
        history.addCommand(command)
    }

    fun moveDown() {
        val command = MoveDownCommand(elevator)
        command.execute()
        history.addCommand(command)
    }

    fun openDoor() {
        val command = OpenDoorCommand(elevator)
        command.execute()
        history.addCommand(command)
    }

    fun closeDoor() {
        val command = CloseDoorCommand(elevator)
        command.execute()
        history.addCommand(command)
    }

    fun undoLast() {
        if (history.undoLast()) {
            println("Отмена последней команды выполнена")
        } else {
            println("История команд пуста")
        }
    }

    fun undoLastNCommands(n: Int) {
        val undone = history.undoLastN(n)
        println("Отменено $undone команд из $n запрошенных")
    }

    fun getCurrentStatus() {
        println("Текущий этаж: ${elevator.getCurrentFloor()}")
        println("Двери: ${if (elevator.isDoorOpen()) "открыты" else "закрыты"}")
        println("История команд: ${history.getHistorySize()}")
    }
}

fun main() {
    val liftControl = LiftControl()

    println("-------------------- Тестирование системы лифта --------------------")

    liftControl.getCurrentStatus()
    println("\n1. Открываем двери")
    liftControl.openDoor()

    println("\n2. Закрываем двери")
    liftControl.closeDoor()

    println("\n3. Поднимаемся на этаж")
    liftControl.moveUp()
    liftControl.moveUp()

    println("\n4. Открываем двери")
    liftControl.openDoor()

    println("\n5. Отменяем последнюю команду")
    liftControl.undoLast()

    println("\n6. Отменяем 3 последние команды")
    liftControl.undoLastNCommands(3)

    println("\n7. Текущий статус:")
    liftControl.getCurrentStatus()
}

/*
 Вопрос: Как вы реализуете отмену нескольких последних команд? Какие ограничения могут возникнуть в вашей системе?
 Ответ: Для отмены нескольких команд реализован метод undoLastN(n: Int) в CommandHistory, который последовательно отменяет n команд.
 Ограничения:
 1. Отмена может работать некорректно, если команды имеют взаимные зависимости (например, открытие дверей зависит от предыдущих движений).
 2. Некоторые команды могут быть не полностью обратимыми (например, если при отмене движения лифт занят).
 3. Большая история команд потребляет память.
 4. Отмена команд должна сохранять инварианты системы (состояние дверей при движении).
 */