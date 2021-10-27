package xyz.theprogramsrc.supermanager.modules.servermanager.guis;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.gui.Gui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiRows;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.servermanager.ServerManager;

public class ServerManagerGUI extends Gui{

    private Consumer<GuiAction> onBack;

    public ServerManagerGUI(Player player, Consumer<GuiAction> onBack){
        super(player, false);
        this.onBack = onBack;
        this.open();
    }

    @Override
    public GuiRows getRows() {
        return GuiRows.FOUR;
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.SERVER_MANAGER_TITLE.toString());
    }

    @Override
    public void onBuild(GuiModel model) {
        model.setButton(model.getRows().size-1, new GuiEntry(this.getPreloadedItems().getBackItem(), this.onBack));
        model.setButton(10, this.motdButton());
        model.setButton(12, this.maxPlayersButton());
        model.setButton(14, this.serverIconButton());
    }

    private GuiEntry motdButton(){
        SimpleItem item = new SimpleItem(XMaterial.OAK_SIGN)
            .setDisplayName("&a" + L.SERVER_MANAGER_CHANGE_MOTD_NAME)
            .setLore(
                "&7",
                "&7" + L.SERVER_MANAGER_CHANGE_MOTD_LORE,
                "&7",
                "&9" + Base.LEFT_CLICK + "&7 " + L.SERVER_MANAGER_CHANGE_MOTD_LINE_1,
                "&9" + Base.RIGHT_CLICK + "&7 " + L.SERVER_MANAGER_CHANGE_MOTD_LINE_2,
                "&7",
                "&a" + L.SERVER_MANAGER_CHANGE_MOTD_PREVIEW,
                "&r" + L.SERVER_MANAGER_CHANGE_MOTD_PREVIEW_LINE_1,
                "&r" + L.SERVER_MANAGER_CHANGE_MOTD_PREVIEW_LINE_2
            ).addPlaceholder("{MOTDLine1}", ServerManager.i.cfg.getString("MOTD.Line1", "&aCustom MOTD!")).addPlaceholder("{MOTDLine2}", ServerManager.i.cfg.getString("MOTD.Line2", "&9Thanks to SuperManager :D"));

        return new GuiEntry(item, a -> {
            if(!(a.clickType == ClickType.LEFT_CLICK || a.clickType == ClickType.RIGHT_CLICK)) return;
            final int lineNumber = a.clickType == ClickType.LEFT_CLICK ? 1 : 2;
            final String title = (lineNumber == 1 ? L.SERVER_MANAGER_CHANGE_MOTD_LINE_1_TITLE : L.SERVER_MANAGER_CHANGE_MOTD_LINE_2_TITLE).toString();
            final String subtitle = (lineNumber == 1 ? L.SERVER_MANAGER_CHANGE_MOTD_LINE_1_SUBTITLE : L.SERVER_MANAGER_CHANGE_MOTD_LINE_2_SUBTITLE).toString();
            final String actionbar = (lineNumber == 1 ? L.SERVER_MANAGER_CHANGE_MOTD_LINE_1_ACTIONBAR : L.SERVER_MANAGER_CHANGE_MOTD_LINE_2_ACTIONBAR).toString();
            new Dialog(a.player) {

                @Override
                public String getTitle() {
                    return title;
                }

                @Override
                public String getSubtitle() {
                    return subtitle;
                }

                @Override
                public String getActionbar() {
                    return actionbar;
                }

                @Override
                public boolean onResult(String in) {
                    ServerManager.i.cfg.set("MOTD.Line" + lineNumber, in);
                    ServerManagerGUI.this.open();
                    return true;
                }
                
            }.addPlaceholder("{CurrentMOTDLine}", ServerManager.i.cfg.getString("MOTD.Line" + lineNumber));
        });
    }

    private GuiEntry maxPlayersButton(){
        SimpleItem item = new SimpleItem(XMaterial.PLAYER_HEAD)
            .setDisplayName("&aMax Players")
            .setLore(
                "&7",
                "&9" + Base.LEFT_CLICK + "&7 " + L.SERVER_MANAGER_MAX_PLAYERS_LEFT_ACTION,
                "&9" + Base.MIDDLE_CLICK + "&7 " + L.SERVER_MANAGER_MAX_PLAYERS_MIDDLE_ACTION,
                "&9" + Base.RIGHT_CLICK + "&7 " + L.SERVER_MANAGER_MAX_PLAYERS_RIGHT_ACTION,
                "&7",
                "&a" + L.SERVER_MANAGER_MAX_PLAYERS_PREVIEW
            ).addPlaceholder("{MaxPlayers}", ServerManager.i.cfg.getInt("MaxPlayers", 100)+"");
        return new GuiEntry(item, a-> {
            if(a.clickType == ClickType.LEFT_CLICK){
                ServerManager.i.cfg.set("MaxPlayers", ServerManager.i.cfg.getInt("MaxPlayers", 100)+1);
                this.open();
            }else if(a.clickType == ClickType.MIDDLE_CLICK){
                new Dialog(a.player) {

                    @Override
                    public String getTitle() {
                        return L.SERVER_MANAGER_MAX_PLAYERS_TITLE.toString();
                    }

                    @Override
                    public String getSubtitle() {
                        return L.SERVER_MANAGER_MAX_PLAYERS_SUBTITLE.toString();
                    }

                    @Override
                    public String getActionbar() {
                        return L.SERVER_MANAGER_MAX_PLAYERS_ACTIONBAR.toString();
                    }

                    @Override
                    public boolean onResult(String in) {
                        try{
                            ServerManager.i.cfg.set("MaxPlayers", Integer.parseInt(in));
                            ServerManagerGUI.this.open();
                            return true;
                        }catch(NumberFormatException e){
                            this.getSuperUtils().sendMessage(a.player, L.INVALID_NUMBER.toString());
                            return false;
                        }
                    }  
                }.addPlaceholder("{CurrentMaxPlayers}", ServerManager.i.cfg.getInt("MaxPlayers", 100)+"");
            }else if(a.clickType == ClickType.RIGHT_CLICK){
                ServerManager.i.cfg.set("MaxPlayers", ServerManager.i.cfg.getInt("MaxPlayers", 100)-1);
                this.open();
            }
        });
    }

    private GuiEntry serverIconButton() {
        SimpleItem item = new SimpleItem(XMaterial.PAINTING)
            .setDisplayName("&a" + L.SERVER_MANAGER_SERVER_ICON_URL_NAME)
            .setLore(
                "&7",
                "&7" + L.SERVER_MANAGER_SERVER_ICON_URL_LORE,
                "&7",
                "&9" + Base.LEFT_CLICK + "&7 " + L.SERVER_MANAGER_SERVER_ICON_URL_LEFT_ACTION,
                "&9" + Base.RIGHT_CLICK + "&7 " + L.SERVER_MANAGER_SERVER_ICON_URL_RIGHT_ACTION
            );

        return new GuiEntry(item, a -> {
            this.close();
            if(a.clickType == ClickType.LEFT_CLICK){
                new Dialog(a.player) {

                    @Override
                    public String getTitle() {
                        return L.SERVER_MANAGER_SERVER_ICON_URL_TITLE.toString();
                    }
    
                    @Override
                    public String getSubtitle() {
                        return L.SERVER_MANAGER_SERVER_ICON_URL_SUBTITLE.toString();
                    }
    
                    @Override
                    public String getActionbar() {
                        return L.SERVER_MANAGER_SERVER_ICON_URL_ACTIONBAR.toString();
                    }
    
                    @Override
                    public boolean onResult(String in) {
                        this.getSpigotTasks().runAsyncTask(() -> {
                            this.getSuperUtils().sendMessage(a.player, L.VALIDATING_SERVER_ICON.toString());
                            // Check if the input 'in' is a valid url
                            if(!in.matches("^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")){
                                this.getSuperUtils().sendMessage(a.player, L.INVALID_SERVER_ICON_URL.toString());
                                return;
                            }
    
                            // Check if the url is online
                            try{
                                new java.net.URL(in).openStream().close();
                            }catch(Exception e){
                                this.getSuperUtils().sendMessage(a.player, L.INVALID_SERVER_ICON_URL.toString());
                                return;
                            }
    
                            // Check if the url is an actual image 
                            try{
                                BufferedImage image = ImageIO.read(new URL(in));
                                if(image == null){
                                    this.getSuperUtils().sendMessage(a.player, L.INVALID_SERVER_ICON_IMAGE.toString());
                                    return;
                                }else {
                                    BufferedImage resized = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
                                    Graphics2D g = resized.createGraphics();
                                    g.drawImage(image, 0, 0, 64, 64, null);
                                    g.dispose();
                                    File server_icon = new File(ServerManager.i.getModuleFolder(), "server_icon.png");
                                    Files.deleteIfExists(server_icon.toPath());
                                    ImageIO.write(resized, "png", server_icon);
                                    ServerManager.i.cfg.set("ServerIcon", server_icon.getAbsolutePath());
                                    this.getSuperUtils().sendMessage(a.player, L.VALIDATING_SERVER_ICON_SUCCESS.toString());
                                }
                            }catch(Exception e){
                                this.getSuperUtils().sendMessage(a.player, L.FAILED_TO_LOAD_SERVER_ICON.toString());
                                return;
                            }
                        });
                        return true;
                    }
                };
            }else if(a.clickType == ClickType.RIGHT_CLICK){
                ServerManager.i.cfg.remove("ServerIcon");
                this.getSuperUtils().sendMessage(a.player, "&a" + Base.DONE);
                this.open();
            }
        });
    }
    
}
