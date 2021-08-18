package xyz.theprogramsrc.supermanager.modules.usermanager.guis;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.gui.Gui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiRows;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserStorage;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

public abstract class UserView extends Gui {

    private final UserStorage userStorage;
    private final User user;

    public UserView(Player player, User user, UserStorage userStorage) {
        super(player, false);
        this.user = user;
        this.userStorage = userStorage;
        this.open();
    }

    public abstract void onBack(GuiAction clickAction);

    @Override
    public GuiRows getRows() {
        return GuiRows.THREE;
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.USER_MANAGER_EDITOR_TITLE.options().placeholder("{UserName}", this.user.getName()).get());
    }

    @Override
    public void onBuild(GuiModel m) {
        m.setButton(this.getRows().size - 1, new GuiEntry(this.getPreloadedItems().getBackItem(), this::onBack));
        m.setButton(18, this.getInformationCardButton());
        m.setButton(0, this.getFreezeButton());
        m.setButton(1, this.getTeleportButton());
        m.setButton(2, this.getViewInventoryButton());
        m.setButton(3, this.getViewECButton());
        m.setButton(4, this.getSendMessageButton());
    }

    private GuiEntry getFreezeButton(){
        boolean frozen = this.user.hasData("frozen") && this.user.getDataAsBoolean("frozen");
        SimpleItem item = new SimpleItem(frozen ? XMaterial.PACKED_ICE : XMaterial.ICE)
                .setDisplayName("&a" + (frozen ? L.USER_MANAGER_EDITOR_UNFREEZE_NAME : L.USER_MANAGER_EDITOR_FREEZE_NAME))
                .setLore(
                        "&7",
                        "&7" + (frozen ? L.USER_MANAGER_EDITOR_UNFREEZE_LORE : L.USER_MANAGER_EDITOR_FREEZE_LORE)
                ).addPlaceholder("{UserName}", this.user.getName());

        return new GuiEntry( item, a->{
            this.close();
            this.userStorage.save(this.user.setData("frozen", !frozen));
            this.getSuperUtils().sendMessage(a.player, L.USER_MANAGER_FREEZE_STATUS.options().placeholder("{Status}", Utils.parseEnabledBoolean(!frozen)+"&r").placeholder("{UserName}", this.user.getName()).get());
        });
    }

    private GuiEntry getTeleportButton(){
        SimpleItem item = new SimpleItem(XMaterial.ENDER_PEARL)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_TELEPORT_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_TELEPORT_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GuiEntry(item, a -> {
            this.close();
            if(!this.user.isOnline()){
                this.getSuperUtils().sendMessage(a.player, L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                this.getSuperUtils().sendMessage(a.player, L.USER_MANAGER_TELEPORTING.options().placeholder("{UserName}", this.user.getName()).get());
                a.player.teleport(this.user.getPlayer().getLocation());
            }
        });
    }

    private GuiEntry getViewInventoryButton(){
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_VIEW_INV_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_VIEW_INV_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GuiEntry(item, a-> {
            if(!this.user.isOnline()){
                this.close();
                this.getSuperUtils().sendMessage(a.player, L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                a.player.openInventory(a.player.getInventory());
            }
        });
    }

    private GuiEntry getViewECButton(){
        SimpleItem item = new SimpleItem(XMaterial.ENDER_CHEST)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_VIEW_ENDER_CHEST_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_VIEW_ENDER_CHEST_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GuiEntry( item, a->{
            if(!this.user.isOnline()){
                this.close();
                this.getSuperUtils().sendMessage(a.player, L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                a.player.openInventory(this.user.getPlayer().getEnderChest());
            }
        });
    }

    private GuiEntry getSendMessageButton(){
        SimpleItem item = new SimpleItem(XMaterial.PAPER)
                .setDisplayName("&a" + L.USER_MANAGER_EDITOR_SEND_MESSAGE_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_EDITOR_SEND_MESSAGE_LORE
                ).addPlaceholder("{UserName}", this.user.getName());
        return new GuiEntry(item, a->{
            if(!this.user.isOnline()){
                this.close();
                this.getSuperUtils().sendMessage(a.player, L.USER_MANAGER_OFFLINE_USER.options().placeholder("{UserName}", this.user.getName()).get());
            }else{
                new Dialog(a.player){
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
                        this.getSuperUtils().sendMessage(a.player, L.USER_MANAGER_MESSAGE_SENT.options().placeholder("{UserName}", UserView.this.user.getName()).get());
                        this.getSuperUtils().sendMessage(a.player, s);
                        return true;
                    }
                }.addPlaceholder("{UserName}", this.user.getName());
            }
        });
    }

    private GuiEntry getInformationCardButton(){
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

        return new GuiEntry(item, a-> this.open());
    }
}
