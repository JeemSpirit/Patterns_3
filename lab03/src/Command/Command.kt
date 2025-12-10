package Command

// Абстрактная команда
abstract class Command(protected val lift: Lift) {
    abstract fun execute()
    abstract fun undo()
}

// Модель лифта
class Lift {
    var floor: Int = 1
        private set

    var doorOpen: Boolean = false
        private set

    fun moveUp() {
        floor++
        println("Лифт поднялся на этаж $floor")
    }

    fun moveDown() {
        if (floor > 1) {
            floor--
            println("Лифт опустился на этаж $floor")
        } else {
            println("Лифт уже на первом этаже")
        }
    }

    fun openDoor() {
        doorOpen = true
        println("Двери лифта открыты")
    }

    fun closeDoor() {
        doorOpen = false
        println("Двери лифта закрыты")
    }
}

// Команда поднятия лифта
class MoveUpCommand(lift: Lift) : Command(lift) {
    override fun execute() {
        lift.moveUp()
    }

    override fun undo() {
        lift.moveDown()
    }
}

// Команда опускания лифта
class MoveDownCommand(lift: Lift) : Command(lift) {
    override fun execute() {
        lift.moveDown()
    }

    override fun undo() {
        lift.moveUp()
    }
}

// Команда открытия дверей
class OpenDoorCommand(lift: Lift) : Command(lift) {
    override fun execute() {
        lift.openDoor()
    }

    override fun undo() {
        lift.closeDoor()
    }
}

// Команда закрытия дверей
class CloseDoorCommand(lift: Lift) : Command(lift) {
    override fun execute() {
        lift.closeDoor()
    }

    override fun undo() {
        lift.openDoor()
    }
}

// История команд
class CommandHistory {
    private val history = ArrayDeque<Command>()

    fun push(command: Command) {
        history.addLast(command)
    }

    fun pop(): Command? {
        return if (history.isNotEmpty()) history.removeLast() else null
    }
}

// Контроллер лифта
class LiftControl(private val lift: Lift) {

    private val history = CommandHistory()

    fun executeCommand(command: Command) {
        command.execute()
        history.push(command)
    }

    fun undoLast() {
        val last = history.pop()
        if (last != null) {
            println("Отмена последней команды")
            last.undo()
        } else {
            println("История пуста, отменять нечего")
        }
    }
}

// Точка входа
fun main() {
    val lift = Lift()
    val controller = LiftControl(lift)

    controller.executeCommand(MoveUpCommand(lift))
    controller.executeCommand(MoveUpCommand(lift))
    controller.executeCommand(OpenDoorCommand(lift))
    controller.executeCommand(CloseDoorCommand(lift))
    controller.executeCommand(MoveDownCommand(lift))

    controller.undoLast()
    controller.undoLast()
    controller.undoLast()
}

// Ответ на вопрос:
// Отмена нескольких последних команд реализуется с помощью структуры данных стек (Command.CommandHistory),
// где каждая выполненная команда помещается в историю. Для отмены нескольких команд подряд
// вызывается undoLast() столько раз, сколько требуется, и каждый раз из стека извлекается последняя команда,
// после чего вызывается ее метод undo().
//
// Ограничения такой системы:
// 1. Не каждая команда может быть безопасно отменена, если она меняет состояние, которое невозможно восстановить
//    (например, необратимые операции, команды, зависящие от внешних ресурсов).
// 2. Объем памяти истории ограничивает количество возможных откатов — слишком большая история приводит к росту памяти.
// 3. Команды должны содержать достаточную информацию для восстановления предыдущего состояния,
//    иначе откат не сможет вернуть систему в корректное состояние.
// 4. Если состояние лифта изменяется вне командного интерфейса, история перестает отражать реальное состояние,
//    и undo может привести к непредсказуемым результатам.
// 5. При сложных зависимостях между командами простой стек откатов может быть недостаточен,
//    так как некоторые операции могут блокировать или влиять на другие.
// Таким образом, система отмены работает корректно при условии,
// что все действия лифта выполняются строго через команды и каждая команда поддерживает обратимость.
