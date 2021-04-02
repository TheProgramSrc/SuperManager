package xyz.theprogramsrc.supermanager.modules.usermanager;

import xyz.theprogramsrc.supercoreapi.SuperPlugin;
import xyz.theprogramsrc.supercoreapi.global.storage.DataBase;
import xyz.theprogramsrc.supercoreapi.global.storage.DataBaseStorage;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UserStorage extends DataBaseStorage {

    private final LinkedHashMap<UUID, User> cache;
    private final String table;

    public UserStorage(SuperPlugin<?> plugin, DataBase dataBase) {
        super(plugin, dataBase);
        this.cache = new LinkedHashMap<>();
        this.table = this.getTablePrefix() + "usermanagermodule_userstorage";
        this.init();
    }

    private void init(){
        new Thread(() -> this.dataBase.connect(c-> {
            try{
                Statement statement = c.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.table + " (uuid VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY, data LONGTEXT NOT NULL);");
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to initialize UserStorage:");
                e.printStackTrace();
            }
        })).start();
    }

    public void save(User user){
        new Thread(() -> {
            this.cache.remove(user.getUUID());
            this.dataBase.connect(c-> {
                try {
                    Statement statement = c.createStatement();
                    if(this.exists(user.getUUID())){
                        statement.executeUpdate("UPDATE "+this.table+" SET data = '" + user.toString() + "' WHERE uuid = '" + user.getUUID() + "';");
                    }else{
                        statement.executeUpdate("INSERT INTO "+this.table+" (uuid, data) VALUES ('" + user.getUUID() + "', '" + user.toString() + "');");
                    }
                }catch (SQLException e){
                    this.plugin.addError(e);
                    this.plugin.log("&cFailed to save user:");
                    e.printStackTrace();
                }
            });
        }).start();
    }

    public boolean exists(UUID uuid){
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        this.dataBase.connect(c-> {
            try {
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT COUNT(DISTINCT uuid) AS `count` FROM " + this.table + " WHERE uuid = '" + uuid + "';");
                if(rs.next()){
                    atomicBoolean.set(rs.getInt("count") > 0);
                }
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to retrieve user:");
                e.printStackTrace();
            }
        });
        return atomicBoolean.get();
    }

    public User get(UUID uuid){
        if(this.cache.containsKey(uuid)) this.cache.get(uuid);
        AtomicReference<User> atomicUser = new AtomicReference<>(null);
        this.dataBase.connect(c-> {
            try {
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT `data` FROM " + this.table + " WHERE uuid = '" + uuid + "';");
                if(rs.next()){
                    atomicUser.set(User.fromJSON(rs.getString("data")));
                }
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to retrieve user:");
                e.printStackTrace();
            }
        });
        if(atomicUser.get() != null) this.cache.put(uuid, atomicUser.get());
        return atomicUser.get();
    }

    public User[] get(){
        LinkedList<User> users = new LinkedList<>();
        this.dataBase.connect(c-> {
            try {
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT `data` FROM " + this.table + ";");
                while(rs.next()){
                    User user = User.fromJSON(rs.getString("data"));
                    this.cache.put(user.getUUID(), user);
                    users.add(user);
                }
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to retrieve users:");
                e.printStackTrace();
            }
        });
        return users.toArray(new User[0]);
    }
}
