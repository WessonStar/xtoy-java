package dev.xtoy.common.process;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 固定容量日志缓冲区，只保留最新的若干行日志。
 */
public class BoundedLogBuffer {
    private final int capacity;
    private final Deque<String> buffer;

    public BoundedLogBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(capacity);
    }

    public synchronized void add(String line) {
        if (buffer.size() >= capacity) {
            buffer.removeFirst();
        }
        buffer.addLast(line);
    }

    public synchronized List<String> snapshot() {
        return new ArrayList<>(buffer);
    }
}

