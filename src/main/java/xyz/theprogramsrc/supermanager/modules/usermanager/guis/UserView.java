package xyz.theprogramsrc.supermanager.modules.usermanager.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserStorage;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

import java.text.DecimalFormat;

public abstract class UserView extends GUI {

    private final UserStorage userStorage;
    private final User user;

    public UserView(Player player, User user, UserStorage userStorage) {
        super(player);
        this.user = user;
        this.userStorage = userStorage;
        this.open();
    }

    public abstract void onBack(ClickAction clickAction);

    @Override
    protected GUIRows getRows() {
        return GUIRows.THREE;
    }

    @Override
    protected String getTitle() {
        return L.USER_MANAGER_EDITOR_TITLE.options().placeholder("{UserName}", this.user.getName()).get();
    }

    @Override
    protected GUIButton[] getButtons() {
        return new GUIButton[]{
                new GUIButton(this.getRows().getSize() - 1, this.getPreloadedItems().getBackItem(), this::onBack),
                this.getInformationCardButton(),
                this.getFreezeButton(),
                this.getTeleportButton(),
                this.getViewInventoryButton(),
                this.getViewECButton(),
                this.getSendMessageButton(),
        };
    }

    private GUIButton getFreezeButton(){
        boolean frozen = this.user.hasData("frozen") && this.user.getDataAsBoolean("frozen");
        SimpleItem item = new SimpleItem(frozen ? XMaterial.PACKED_ICE : XMaterial.ICE)
                .setDisplayName("&a" + (frozen ? L.USER_MANAGER_EDITOR_UNFREEZE_NAME : L.USER_MANAGER_EDITOR_FREEZE_NAME))
                .setLore(
                        "&7",
                        "&7" + (frozen ? L.USER_MANAGER_EDITOR_UNFREEZE_LORE : L.USER_MANAGER_EDITOR_FREEZE_LORE)
                ).addPlaceholder("{UserName}", this.user.getName());

        return new GUIButton(0, item, a->{
            this.close();
            this.userStorage.save(this.user.setData("frozen", !frozen));
            this.getSuperUtils().sendMessage(a.getPlayer(), L.USER_MANAGER_FREEZE_STATUS.options().placeholder("{Status}", Utils.parseEnabledBoolean(!frozen)+"&r").placeholder("{UserName}", this.user.getName()).get());
        });
    }

    private GUIButton getTeleportButton(){
        SimpleItem item = new SimpleItem(XMaterial.ENDER_PEARL)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_TELEPORT_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_TELEPORT_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GUIButton(1, item, a->{
            this.close();
            if(!this.user.isOnline()){
                this.getSuperUtils().sendMessage(a.getPlayer(), L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                this.getSuperUtils().sendMessage(a.getPlayer(), L.USER_MANAGER_TELEPORTING.options().placeholder("{UserName}", this.user.getName()).get());
                a.getPlayer().teleport(this.user.getPlayer().getLocation());
            }
        });
    }

    private GUIButton getViewInventoryButton(){
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_VIEW_INV_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_VIEW_INV_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GUIButton(2, item, a-> {
            if(!this.user.isOnline()){
                this.close();
                this.getSuperUtils().sendMessage(a.getPlayer(), L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                a.getPlayer().openInventory(this.getPlayer().getInventory());
            }
        });
    }

    private GUIButton getViewECButton(){
        SimpleItem item = new SimpleItem(XMaterial.ENDER_CHEST)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_VIEW_ENDER_CHEST_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_VIEW_ENDER_CHEST_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GUIButton(3, item, a->{
            if(!this.user.isOnline()){
                this.close();
                this.getSuperUtils().sendMessage(a.getPlayer(), L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                a.getPlayer().openInventory(this.user.getPlayer().getEnderChest());
            }
        });
    }

    private GUIButton getSendMessageButton(){
        SimpleItem item = new SimpleItem(XMaterial.PAPER)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_SEND_MESSAGE_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_SEND_MESSAGE_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GUIButton(4, item, a->{
            if(!this.user.isOnline()){
                this.close();
                this.getSuperUtils().sendMessage(a.getPlayer(), L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                new Dialog(a.getPlayer()){
                    @Override
                    public String getTitle() {
                        return L.USER_MANAGER_EDITOR_SEND_MESSAGE_DIALOG_TITLE.toString();
                    }

                    @Override
                    public String getSubtitle() {
                        return L.USER_MANAGER_EDITOR_SEND_MESSAGE_DIALOG_SUBTITLE.toString();
                    }

                    @Override
                    public String getActionbar() {
                        return L.USER_MANAGER_EDITOR_SEND_MESSAGE_DIALOG_ACTIONBAR.toString();
                    }

                    @Override
                    public boolean onResult(String s) {
                        this.getSuperUtils().sendMessage(UserView.this.user.getPlayer(), s);
                        this.getSuperUtils().sendMessage(this.getPlayer(), L.USER_MANAGER_MESSAGE_SENT.options().placeholder("{UserName}", UserView.this.user.getName()).get());
                        this.getSuperUtils().sendMessage(this.getPlayer(), s);
                        return true;
                    }
                }.addPlaceholder("{UserName}", this.user.getName());
            }
        });
    }

    private GUIButton getInformationCardButton(){
        SimpleItem item = new SimpleItem(XMaterial.PAPER)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_INFORMATION_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_INFORMATION_POSITION,
                        "&7" + L.USER_MANAGER_EDITOR_INFORMATION_HEALTH,
                        "&7" + L.USER_MANAGER_EDITOR_INFORMATION_FOOD
                )
                .addPlaceholder("{UserName}", this.user.getName())
                .addPlaceholder("{Player}", this.user.getName())
                .addPlaceholder("{UUID}", this.user.getUUID().toString());
        if(this.user.isOnline()){
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            Player player = this.user.getPlayer();
            item.addPlaceholder("{World}", player.getWorld().getName())
                    .addPlaceholder("{POS_X}", decimalFormat.format(player.getLocation().getX()))
                    .addPlaceholder("{POS_Y}", decimalFormat.format(player.getLocation().getY()))
                    .addPlaceholder("{POS_Z}", decimalFormat.format(player.getLocation().getZ()))
                    .addPlaceholder("{HealthLevel}", player.getHealth()+"")
                    .addPlaceholder("{FoodLevel}", player.getFoodLevel()+"")
                    .addPlaceholder("{DisplayName}", player.getDisplayName());
        }else{
            item.addPlaceholder("{World}", L.UNKNOWN_WORLD.toString())
                    .addPlaceholder("{POS_X}", "?")
                    .addPlaceholder("{POS_Y}", "?")
                    .addPlaceholder("{POS_Z}", "?")
                    .addPlaceholder("{HealthLevel}", "??")
                    .addPlaceholder("{FoodLevel}", "??")
                    .addPlaceholder("{DisplayName}", this.user.getName());
        }

        return new GUIButton(18, item, a-> this.open());
    }
}
