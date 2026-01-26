package com.oriooneee.jet.navigation.engine.utils

class MinHeap<T>(private val comparator: Comparator<T>) {
    private val heap = ArrayList<T>()

    fun isNotEmpty(): Boolean = heap.isNotEmpty()

    fun offer(element: T) {
        heap.add(element)
        siftUp(heap.size - 1)
    }

    fun poll(): T? {
        if (heap.isEmpty()) return null
        val result = heap[0]
        val last = heap.removeAt(heap.size - 1)
        if (heap.isNotEmpty()) {
            heap[0] = last
            siftDown(0)
        }
        return result
    }

    private fun siftUp(index: Int) {
        var k = index
        while (k > 0) {
            val parent = (k - 1) / 2
            if (comparator.compare(heap[k], heap[parent]) >= 0) break
            swap(k, parent)
            k = parent
        }
    }

    private fun siftDown(index: Int) {
        var k = index
        val half = heap.size / 2
        while (k < half) {
            var child = 2 * k + 1
            val right = child + 1
            if (right < heap.size && comparator.compare(heap[right], heap[child]) < 0) {
                child = right
            }
            if (comparator.compare(heap[k], heap[child]) <= 0) break
            swap(k, child)
            k = child
        }
    }

    private fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
    }
}
