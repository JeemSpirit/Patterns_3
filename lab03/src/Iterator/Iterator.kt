package Iterator

// Модель товара
data class Product(
    val name: String,
    val category: String,
    val price: Double,
    val popularity: Int
)

// Интерфейс итератора
interface CatalogIterator {
    fun hasNext(): Boolean
    fun next(): Product?
    fun next(count: Int): List<Product>
    fun getNext(count: Int): List<Product>
    fun reset()
}

// Базовый класс для итераторов
abstract class BaseIterator(
    protected val products: List<Product>
) : CatalogIterator {

    protected var index = 0

    override fun reset() {
        index = 0
    }

    override fun next(count: Int): List<Product> {
        val result = getNext(count)
        return result
    }

    override fun getNext(count: Int): List<Product> {
        val result = mutableListOf<Product>()
        var c = count

        while (hasNext() && c > 0) {
            result.add(products[index])
            index++
            c--
        }
        return result
    }
}

// Итератор по категориям
class CategoryIterator(
    all: List<Product>,
    private val category: String
) : BaseIterator(all.filter { it.category == category }) {

    override fun hasNext(): Boolean = index < products.size

    override fun next(): Product? {
        return if (hasNext()) products[index++] else null
    }
}

// Итератор по цене (от дешевых к дорогим)
class PriceIterator(
    all: List<Product>
) : BaseIterator(all.sortedBy { it.price }) {

    override fun hasNext(): Boolean = index < products.size

    override fun next(): Product? {
        return if (hasNext()) products[index++] else null
    }
}

// Итератор по популярности
class PopularityIterator(
    all: List<Product>
) : BaseIterator(all.sortedByDescending { it.popularity }) {

    override fun hasNext(): Boolean = index < products.size

    override fun next(): Product? {
        return if (hasNext()) products[index++] else null
    }
}

// Каталог, использующий текущий итератор
class Catalog(private val products: List<Product>) {

    private var iterator: CatalogIterator? = null

    fun setIterator(it: CatalogIterator) {
        iterator = it
    }

    fun reset() {
        iterator?.reset()
    }

    fun getNext(): Product? {
        return iterator?.next()
    }

    fun getNext(count: Int): List<Product> {
        return iterator?.getNext(count) ?: emptyList()
    }
}

// Точка входа
fun main() {
    val items = listOf(
        Product("Товар A", "Электроника", 1200.0, 50),
        Product("Товар B", "Электроника", 900.0, 20),
        Product("Товар C", "Одежда", 1500.0, 10),
        Product("Товар D", "Одежда", 500.0, 70)
    )

    val catalog = Catalog(items)

    catalog.setIterator(CategoryIterator(items, "Одежда"))
    println(catalog.getNext(2))

    catalog.setIterator(PriceIterator(items))
    println(catalog.getNext())

    catalog.setIterator(PopularityIterator(items))
    println(catalog.getNext(3))
}

// Ответ на вопрос:
// Если в каталоге нет товаров, соответствующих определенному критерию (например, категория пустая),
// итератор должен возвращать пустой набор данных. Для этого фильтрация товаров выполняется заранее,
// и если результат фильтрации пустой, итератор работает с пустым списком.
// Метод hasNext() в таком случае сразу возвращает false, а методы next() и getNext()
// возвращают null или пустой список.
// Изменения, которые вносятся в систему:
// 1. Фильтрация и сортировка выполняются при создании итератора, поэтому отсутствие товаров
//    автоматически обрабатывается без ошибок.
// 2. Каталог должен корректно обрабатывать ситуацию, когда итератор возвращает пустые данные,
//    чтобы интерфейс программы не ломался.
// 3. При необходимости можно добавить механизм уведомления пользователя, что товаров не найдено.
// Таким образом, система устойчиво работает даже при отсутствии подходящих товаров.
