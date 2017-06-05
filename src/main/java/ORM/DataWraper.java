package ORM;

import Laba2.Leg;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SlyFox on 29.05.2017.
 */
public class DataWraper {
    private Connection con;
    public Map<String,DataTable> tables;

    private Map.Entry<String,String>  checkPrimaryKey(Field field){
        if((field.getAnnotation(Atribute.class)).isPrimaryKey()) {
            return new AbstractMap.SimpleEntry<String, String>((field.getAnnotation(Atribute.class)).name(),
                    (field.getAnnotation(Atribute.class)).type());
        }
        return null;
    }

    private Map<String,String> checkPrimaryKey(Field field,  Map<String, String> atribute){
        if((field.getAnnotation(Atribute.class)).isPrimaryKey()) {
            return atribute;
        }
        return null;
    }

    private Map.Entry<String, String> addAtribute(String name, Atribute attribute){
        return new AbstractMap.SimpleEntry<String, String>(name + " ",
                attribute.type()
        );
    }

    private Map<String, String> addAtribute(DataTable target, String name){
        Map<String, String> temp = new HashMap<>();

        target.primaryKeys.forEach((i,j) ->
            temp.put(name + "_" + target.name + "_" + i + "_id", j)
        );
        return temp;
    }

    private Map.Entry<String, Set<String>> addReferences(DataTable target, Map<String, String> atributes){
        return new AbstractMap.SimpleEntry<String, Set<String>>(target.name, atributes.keySet());
    }

    private void createAssociativeDataTable(DataTable left, DataTable right) {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "";
        StringJoiner joiner = new StringJoiner(", ");
        StringJoiner leftPrimary = new StringJoiner(", ");
        StringJoiner rightPrimary = new StringJoiner(", ");

        left.primaryKeys.forEach((i,j) ->{
            joiner.add(left.name+"_"+ i+"_id" + " " + j);
            leftPrimary.add(left.name+"_"+ i+"_id");
        });
        right.primaryKeys.forEach((i,j) -> {
            joiner.add(right.name + "_" + i + "_id" + " " + j);
            rightPrimary.add(right.name + "_" + i + "_id");
        });


        sql = "CREATE TABLE " + left.name + "_" + right.name + "(" + joiner + ", " +
                "PRIMARY KEY(" + leftPrimary + ", " + rightPrimary + " ), " +
                "FOREIGN KEY(" + leftPrimary + " ) REFERENCES " + left.name + ", " +
                "FOREIGN KEY(" + rightPrimary + ") REFERENCES "+ right.name + ");";
        try {
            statment.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public DataTable createDataTableTrns(Class target, DataTable reference, Atribute ref)
    {
        try {
            con.setAutoCommit(false);
            DataTable table = createDataTable(target, reference, ref);
            con.commit();
            con.setAutoCommit(true);
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataTable createDataTable(Class target, DataTable reference, Atribute ref) {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql;
            DataTable dataTable = new DataTable(((Entity)target.getAnnotation(Entity.class)).name());
            Map<Class, Atribute> defered = new HashMap<>();

            Field[] publicFields = target.getDeclaredFields();
            for (int i = 0; i < 2; i++) {
                for (Field field : publicFields) {
                    field.setAccessible(true);

                    if(field.isAnnotationPresent(Atribute.class)) {
                        switch ((field.getAnnotation(Atribute.class)).relation()){
                            case OneToOne: {
                                DataTable temp = createDataTable(Class.forName((field.getAnnotation(Atribute.class)).type()), null,null);
                                Map<String, String> atributeTemp = addAtribute(temp, (field.getAnnotation(Atribute.class)).name());
                                dataTable.atributes.putAll(atributeTemp);
                                Map.Entry<String, Set<String>> referenceTemp = addReferences(temp,atributeTemp);

                                dataTable.references.put(referenceTemp.getKey(), referenceTemp.getValue());

                                Map<String,String> tempKey = checkPrimaryKey(field, atributeTemp);
                                if(tempKey != null)
                                    dataTable.primaryKeys.putAll(tempKey);
                                break;
                            }
                            case Primitive:{
                                Map.Entry<String, String> atributeTemp = addAtribute((field.getAnnotation(Atribute.class)).name(), field.getAnnotation(Atribute.class));
                                dataTable.atributes.put(atributeTemp.getKey(), atributeTemp.getValue());

                                Map.Entry<String, String> primaryKeyTemp = checkPrimaryKey(field);
                                if(primaryKeyTemp != null)
                                    dataTable.primaryKeys.put(primaryKeyTemp.getKey(), primaryKeyTemp.getValue());
                                break;
                            }
                            case OneToMany: {
                                //TODO а map как будет отображаться?
                                if ((field.getAnnotation(Atribute.class)).type().equals("entity")) {
                                    defered.put(Class.forName((field.getAnnotation(Atribute.class)).name()), field.getAnnotation(Atribute.class));
                                } else {
                                    //TODO
                                    System.out.println("Давайте без этого");
                                }
                                break;
                            }
                            case ManyToMany: {
                                DataTable temp = createDataTable(Class.forName((field.getAnnotation(Atribute.class)).name()), null, null);
                                dataTable.manyToManyReferences.add(temp);
                            }
                        }
                    }
                }
                publicFields = target.getSuperclass().getDeclaredFields();
            }

            if(dataTable.primaryKeys.size() == 0) {
                dataTable.atributes.put("ID", "SERIAL");
                dataTable.primaryKeys.put("ID", "SERIAL");
            }

            Map<String, String> foreignKeys = new HashMap<>();
            if(ref!= null)
            {
                reference.primaryKeys.forEach((i,j) ->
                    foreignKeys.put(reference.name+ "_" + i + "_id", j)
                );
                dataTable.atributes.putAll(foreignKeys);
                if(ref.isPrimaryKey())
                {
                    dataTable.primaryKeys.putAll(foreignKeys);
                }
            }

            StringJoiner tempAtribute = new StringJoiner(",");
            dataTable.atributes.forEach((i,j)->
                    tempAtribute.add(i+" "+j)
            );

            StringJoiner tempReference = new StringJoiner(",");
            dataTable.references.forEach((i, j) ->
                    tempReference.add("FOREIGN KEY ("+ String.join(",", j) + ") REFERENCES " + i + " ON DELETE CASCADE ON UPDATE CASCADE")
            );

            sql = "CREATE TABLE "+
                    ((Entity)target.getAnnotation(Entity.class)).name()+
                    " (" + tempAtribute +
                    ", PRIMARY KEY ( " + String.join(", ", dataTable.primaryKeys.keySet()) +
                    ")" +
                        (ref != null ? (", " +
                        "FOREIGN KEY ("+ String.join(",",  foreignKeys.keySet()) + ") REFERENCES " + reference.name) + " ON DELETE CASCADE ON UPDATE CASCADE" : "") +
                        ( tempReference.toString().equals("")? "" : (", " + tempReference)) +
                    ");";
            statment.execute(sql);



            StringJoiner joiner = new StringJoiner(", ");
            dataTable.primaryKeys.forEach((j,k) -> joiner.add( ((Entity)target.getAnnotation(Entity.class)).name() + "_"+j+"_id "+ k));


            for(DataTable i : dataTable.manyToManyReferences){
                createAssociativeDataTable(dataTable, i);
            }

            Statement finalStatment = statment;
            defered.forEach((i, j)-> {
                try {
                    DataTable temp = createDataTable(i, dataTable, j);
                    String str = "ALTER TABLE " + temp.name + " ADD " + dataTable.name + "_" + temp.name + "_index integer;";
                    finalStatment.execute(str);

                    /*if(j.isPrimaryKey())
                    {
                        String str = "ALTER TABLE " + temp.name + " DROP CONSTRAINT " + temp.name +"_pkey;";
                        finalStatment.execute(str);

                        StringJoiner keys = new StringJoiner(",");
                        dataTable.primaryKeys.forEach((m,n)-> {
                            try {
                                String s = dataTable.name + "_" + j.name() + "_" + m + "_id";
                                keys.add(s);
                                finalStatment.execute("ALTER TABLE " + temp.name + " ADD "+ s + ";");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });

                        str = "ALTER TABLE " + temp.name + " ADD PRIMARY KEY ("   ");";
                        finalStatment.execute(str);
                    }*/

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            tables.put(dataTable.name, dataTable);

            return dataTable;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("Не пытайся вставить лишнего!");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> addRecordTrans(Object record, DataTable mappedTable, DataTable reference)
    {
        try {
            con.setAutoCommit(false);
            List<String> table = addRecord(record, mappedTable, reference);
            con.commit();
            con.setAutoCommit(true);
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getPrimaryKeys(Object target)
    {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql;
            List<String> values = new LinkedList<>();
            Field[] publicFields = target.getClass().getDeclaredFields();
            for (int i = 0; i < 2; i++) {
                for (Field field : publicFields) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Atribute.class) &&
                            field.getAnnotation(Atribute.class).isPrimaryKey() &&
                            (field.getAnnotation(Atribute.class).relation() == Relation.OneToOne ||
                            field.getAnnotation(Atribute.class).relation() == Relation.Primitive)) {
                        values.add(field.get(target).toString());
                    }
                }
                publicFields = target.getClass().getSuperclass().getDeclaredFields();
            }
            return values;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> addRecord(Object record, DataTable mappedTable, DataTable reference) {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Savepoint save = null;
        try {
            save = con.setSavepoint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            String sql;

            List<String> values = new LinkedList<>();
            List<String> primaryValues = new LinkedList<>();
            Map<Map.Entry<Field, Integer>,List<String>> Defered = new HashMap<>();

            Field[] publicFields = record.getClass().getDeclaredFields();
            for (int i = 0; i < 2; i++) {
                for (Field field : publicFields) {
                    field.setAccessible(true);

                    if (field.isAnnotationPresent(Atribute.class)) {
                        switch ((field.getAnnotation(Atribute.class)).relation()) {
                            case OneToOne: {
                                List<String> prim = addRecord(field.get(record), tables.get(field.getAnnotation(Atribute.class).Reference()), null);

                                if(prim == null)
                                {
                                    prim = getPrimaryKeys(field.get(record));
                                }

                                values.addAll(prim);
                                if((field.getAnnotation(Atribute.class)).isPrimaryKey()){
                                    primaryValues.addAll(prim);
                                }
                                break;
                            }
                            case Primitive: {
                                values.add("'"+field.get(record).toString()+"'");
                                if((field.getAnnotation(Atribute.class)).isPrimaryKey()){
                                    primaryValues.add("'"+field.get(record).toString()+"'");
                                }
                                break;
                            }
                            case OneToMany: {
                                Iterable<Object> iterator;
                                if(field.getType().isArray()){
                                    iterator = Arrays.asList((Object[])field.get(record));
                                }
                                else{
                                    iterator = (Iterable<Object>)field.get(record);
                                }
                                if ((field.getAnnotation(Atribute.class)).type().equals("entity")) {
                                    int index = 0;
                                    for (Object element : iterator) {
                                        try {
                                            List<String> prim = addRecord(element, tables.get(Class.forName((field.getAnnotation(Atribute.class)).name()).getAnnotation(Entity.class).name()), mappedTable);

                                            Iterator<String> second  = prim.iterator();
                                            Iterator<String> first = tables.get(Class.forName((field.getAnnotation(Atribute.class)).name()).getAnnotation(Entity.class).name()).primaryKeys.keySet().iterator();

                                            StringJoiner keys = new StringJoiner(" AND ");

                                            for(;first.hasNext();){
                                                String firstIt = first.next();
                                                String secondIt = second.next();

                                                keys.add(firstIt + " = '" + secondIt+"'");
                                            }

                                            String str = "UPDATE " +
                                                tables.get(Class.forName((field.getAnnotation(Atribute.class)).name()).getAnnotation(Entity.class).name()).name+
                                                " SET " + (record.getClass().getAnnotation(Entity.class) !=null?
                                                        record.getClass().getAnnotation(Entity.class).name():
                                                        record.getClass().getSuperclass().getAnnotation(Entity.class).name())+
                                                    "_"+tables.get(Class.forName((field.getAnnotation(Atribute.class)).name()).getAnnotation(Entity.class).name()).name+
                                                    "_index = " + index + " WHERE " + keys + ";";
                                            statment.execute(str);
                                            Defered.put(new AbstractMap.SimpleEntry<>(field, index) , prim);
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        index++;
                                    }
                                }
                                else {
                                    for (Object element : iterator) {
                                        //TODO пока бех этого
                                    }
                                }
                                break;
                            }
                            case ManyToMany: {
                                break;
                            }
                        }
                    }
                }
                publicFields = record.getClass().getSuperclass().getDeclaredFields();
            }

            if(primaryValues.size() == 0){
                values.add("DEFAULT");
            }

            sql = "INSERT INTO " + (record.getClass().getAnnotation(Entity.class) != null?
                        record.getClass().getAnnotation(Entity.class).name():
                        record.getClass().getSuperclass().getAnnotation(Entity.class).name())
                    + " VALUES (" + String.join("," ,values) +");";

            statment.execute(sql, Statement.RETURN_GENERATED_KEYS);

            if(primaryValues.size() == 0) {
                ResultSet resultSet = statment.getGeneratedKeys();
                if (resultSet.next()) {
                    primaryValues.add(resultSet.getString("ID"));
                }
            }

            Statement finalStatment = statment;
            Defered.forEach((i, j)->{
                try {
                    Iterator<String> second  = j.iterator();
                    Iterator<String> first = tables.get(Class.forName((i.getKey().getAnnotation(Atribute.class)).name()).getAnnotation(Entity.class).name()).primaryKeys.keySet().iterator();

                    StringJoiner keys = new StringJoiner(" AND ");

                    for(;first.hasNext();){
                        String firstIt = first.next();
                        String secondIt = second.next();

                        keys.add(firstIt + " = " + secondIt);
                    }

                    StringJoiner value = new StringJoiner(",");
                    mappedTable.primaryKeys.keySet().forEach((string) ->{
                        value.add(((record.getClass().getAnnotation(Entity.class) !=null?
                                record.getClass().getAnnotation(Entity.class).name():
                                record.getClass().getSuperclass().getAnnotation(Entity.class).name())+
                                "_" + string +
                                "_id"));
                    });

                    String str = "UPDATE " +
                            tables.get(Class.forName((i.getKey().getAnnotation(Atribute.class)).name()).getAnnotation(Entity.class).name()).name+
                            " SET (" + value + ") = (" + String.join(",", primaryValues) + ") WHERE " + keys + ";";
                    finalStatment.execute(str);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });

            return primaryValues;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                con.rollback(save);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getRecordTrans(Class target, List<String> primaryValues)
    {
        try {
            con.setAutoCommit(false);
            Object table = getRecord(target, primaryValues);
            con.commit();
            con.setAutoCommit(true);
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getRecord(Class target, List<String> primaryValues) {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql;

            Object object = target.newInstance();

            String tableName = target.getAnnotation(Entity.class) != null?
                    ((Entity)target.getAnnotation(Entity.class)).name() :
                    ((Entity)target.getSuperclass().getAnnotation(Entity.class)).name();
            DataTable dataTable = tables.get(tableName);

            Iterator<String> second  = primaryValues.iterator();
            Iterator<String> first = dataTable.primaryKeys.keySet().iterator();

            StringJoiner keys = new StringJoiner(" AND ");

            for(;first.hasNext();){
                String firstIt = first.next();
                String secondIt = second.next();

                keys.add(firstIt + " = '" + secondIt + "'");
            }

            sql = "SELECT * FROM " + tableName +
                    " WHERE "+ keys +" ;";
            ResultSet data =  statment.executeQuery(sql);
            if(data.next()){
                Field[] publicFields = target.getDeclaredFields();
                for (int i = 0; i < 2; i++) {
                    for (Field field : publicFields) {
                        field.setAccessible(true);

                        if (field.isAnnotationPresent(Atribute.class)) {
                            switch ((field.getAnnotation(Atribute.class)).relation()) {
                                case OneToOne: {
                                    Pattern pattern = Pattern.compile("^"+(field.getAnnotation(Atribute.class)).name()+ "_" +
                                            Class.forName((field.getAnnotation(Atribute.class)).type()).getAnnotation(Entity.class).name()+".*_id");

                                    List<String> primRefKeys = new LinkedList<>();

                                    dataTable.atributes.forEach((key, value) ->{
                                        if (pattern.matcher(key).matches()){
                                            try {
                                                primRefKeys.add(data.getObject(key.toLowerCase()).toString());
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    field.set(object, getRecord(Class.forName((field.getAnnotation(Atribute.class)).type()), primRefKeys));
                                    break;
                                }
                                case Primitive: {
                                    if(field.getType().isEnum()){
                                        //field.getAnnotation(Atribute.class).type()
                                        field.set(object,
                                                Enum.valueOf((Class<? extends Enum>)Class.forName(field.getType().getName()),
                                                        data.getObject((field.getAnnotation(Atribute.class)).name()).toString()));
                                    }
                                    else{
                                        field.set(object, data.getObject((field.getAnnotation(Atribute.class)).name()));
                                    }
                                    break;
                                }
                                case OneToMany: {
                                    List list = new LinkedList<>();

                                    Pattern pattern = Pattern.compile("^"+ tableName + "_" + ".*_id");
                                    List<String> primRefKeys = new LinkedList<>();

                                    tables.get(Class.forName(field.getAnnotation(Atribute.class).name()).getAnnotation(Entity.class).name())
                                            .atributes.forEach((key, value) ->{
                                        if (pattern.matcher(key).matches()){
                                            primRefKeys.add(key.toLowerCase());
                                        }
                                    });

                                    Iterator<String> firstRef = primRefKeys.iterator();
                                    Iterator<String> secondRef = primaryValues.iterator();

                                    StringJoiner keysRef = new StringJoiner(" AND ");

                                    for(;firstRef.hasNext();){
                                        String firstIt = firstRef.next();
                                        String secondIt = secondRef.next();

                                        keysRef.add(firstIt + " = '" + secondIt + "'");
                                    }

                                    String refTableName = Class.forName(field.getAnnotation(Atribute.class).name()).getAnnotation(Entity.class).name();

                                    sql = "SELECT "+ String.join(",", tables.get(refTableName).primaryKeys.keySet()) + " FROM " + refTableName +
                                            " WHERE "+ keysRef +" ;";

                                    ResultSet reference = con.createStatement().executeQuery(sql);

                                    while(reference.next()){
                                        List<String> refPrimatyValue = new LinkedList<>();
                                        tables.get(refTableName).primaryKeys.forEach((key, value) -> {
                                            try {
                                                refPrimatyValue.add(reference.getString(key));
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        });

                                        list.add(getRecord(Class.forName(field.getAnnotation(Atribute.class).name()), refPrimatyValue));
                                    }

                                    if(field.getType().isArray()) {

                                        field.set(object, list.toArray((Object[]) java.lang.reflect.Array.newInstance(field.getType().getComponentType(), 1)));
                                        //list.toArray((Object[])field.get(object));
                                    }
                                    else {
                                        field.set(object, list);
                                    }
                                    break;
                                }
                                case ManyToMany: {

                                    break;
                                }
                            }
                        }
                    }
                    publicFields = target.getSuperclass().getDeclaredFields();
                }
            }
            return object;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteRecrdTrans(Class target, List<String> primaryValues)
    {
        try {
            con.setAutoCommit(false);
            deleteRecrd(target, primaryValues);
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    public void deleteRecrd(Class target, List<String> primaryValues)
    {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql;

            String tableName = target.getAnnotation(Entity.class) != null ?
                    ((Entity) target.getAnnotation(Entity.class)).name() :
                    ((Entity) target.getSuperclass().getAnnotation(Entity.class)).name();
            DataTable dataTable = tables.get(tableName);

            Iterator<String> second = primaryValues.iterator();
            Iterator<String> first = dataTable.primaryKeys.keySet().iterator();

            StringJoiner keys = new StringJoiner(" AND ");

            for (; first.hasNext(); ) {
                String firstIt = first.next();
                String secondIt = second.next();

                keys.add(firstIt + " = '" + secondIt + "'");
            }

            sql = "DELETE FROM " + tableName +
                    " WHERE " + keys + " ;";
            statment.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List getAll(Class target)
    {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String className = (Entity)target.getAnnotation(Entity.class) != null? ((Entity)target.getAnnotation(Entity.class)).name()
                    : ((Entity)target.getSuperclass().getAnnotation(Entity.class)).name();
            String sql = "SELECT " + String.join(",", tables.get(className).primaryKeys.keySet())
                    + " FROM " + className + ";";
            ResultSet resultSet = statment.executeQuery(sql);
            List objects = new LinkedList();
            while(resultSet.next())
            {
                List<String> refPrimatyValue = new LinkedList<>();
                tables.get(className).primaryKeys.forEach((key, value) -> {
                    try {
                        refPrimatyValue.add(resultSet.getString(key));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                objects.add(getRecordTrans(target, refPrimatyValue));
            }
            return objects;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public DataWraper()
    {
        tables = new HashMap<>();
        try {
        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres" +
                "", "postgres", "vbntkmigbkm1");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void Delete()
    {
        Statement statment = null;
        try {
            statment = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql;
        try {
            sql = "DROP TABLE LEGS";
            statment.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            sql = "DROP TABLE Person";
            statment.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            sql = "DROP TABLE Place";
            statment.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
