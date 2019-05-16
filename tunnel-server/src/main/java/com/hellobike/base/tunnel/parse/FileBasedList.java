package com.hellobike.base.tunnel.parse;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.util.*;

/**
 * 基于 mem + file 存储的 list
 * 当 memList.size() == threshold 时, flush到文件存储
 */
@NotThreadSafe
public class FileBasedList<E> implements Collection<E> {

    // 存储元素的文件列表
    private List<File> files = new ArrayList<>();
    private int threshold;
    private List<E> memList = new ArrayList<>();
    private int size = 0;

    public FileBasedList() {
        this(10000);
    }

    public FileBasedList(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (E e : this) {
            if (e.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            int currFileIndex = 0;
            Iterator<E> iterator;
            Collection<E> currColl = null;
            boolean memListIterated = false;

            @Override
            public boolean hasNext() {
                if(size() > 0) {
                    if(currFileIndex < files.size()) {
                        try {
                            if (currColl == null || !iterator.hasNext()) {
                                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(files.get(currFileIndex++)));
                                currColl = (Collection<E>) ois.readObject();
                                ois.close();
                                iterator = currColl.iterator();
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (iterator == null || (!iterator.hasNext() && !memListIterated)) {
                        iterator = memList.iterator();
                        memListIterated = true;
                    }
                    return iterator.hasNext();
                }
                return false;
            }

            @Override
            public E next() {
                return iterator.next();
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Unsupported toArray for the collection since may be OOM error");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Unsupported toArray for the collection since may be OOM error");
    }

    @Override
    public boolean add(E e) {
        return addAll(Arrays.asList(e));
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.size() + memList.size() < threshold) {
            memList.addAll(c);
            size += c.size();
        } else {
            Iterator<?> iterator = c.iterator();
            while (iterator.hasNext()) {
                if (threshold == memList.size()){
                    flush();
                } else {
                    memList.add((E) iterator.next());
                    size++;
                }
            }
        }
        return true;
    }

    public void flush() {
        File file;
        try {
            file = File.createTempFile("tunnel", null);
            file.deleteOnExit();
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
            stream.writeObject(memList);
            stream.close();
            files.add(file);
            memList.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        files.forEach(file -> {
            if(file.exists()) file.delete();
        });
        files.clear();
        memList.clear();
        size = 0;
    }
}
