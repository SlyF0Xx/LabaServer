package ORM;

import java.util.*;

/**
 * Created by SlyFox on 30.05.2017.
 */
public class DataTable {
    String name;
    Map<String,String> primaryKeys;
    Map<String, String> atributes = new HashMap<>();
    List<DataTable> manyToManyReferences = new LinkedList<>();

    //TODO нужна структура, которая при добавлении по тому же ключу к листу добавит новое значение
    Map<String, Set<String>> references = new HashMap<>();

    public DataTable(){
        this.primaryKeys = new LinkedHashMap<>();
        this.atributes = new LinkedHashMap<>();
        this.manyToManyReferences = new LinkedList<>();
        this.references = new HashMap<>();
    }

    public DataTable(String name){
        this.primaryKeys = new LinkedHashMap<>();
        this.name = name;
        this.atributes = new LinkedHashMap<>();
        this.manyToManyReferences = new LinkedList<>();
        this.references = new HashMap<>();
    }

    public DataTable(Map<String,String> primaryKeys, String name){
        this.primaryKeys = primaryKeys;
        this.name = name;
        this.atributes = new LinkedHashMap<>();
        this.manyToManyReferences = new LinkedList<>();
        this.references = new HashMap<>();
    }
}
