package xyz.theprogramsrc.supermanager.modules.filemanager.guis.editors;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.filemanager.objects.YMLField;

public class YMLEditor extends BrowserGui<YMLField> {

    private final YMLConfig cfg;
    private final File file;
    private final LinkedHashMap<String, Integer> currentLine;

    public YMLEditor(Player player, File file) {
        super(player, false);
        this.file = file;
        this.cfg = new YMLConfig(this.file);
        this.backEnabled = true;
        this.currentLine = new LinkedHashMap<>();
        this.open();
    }

    @Override
    public YMLField[] getObjects() {
        return this.cfg.getKeys(true).stream().map(path -> new YMLField(this.file, path)).sorted((f1, f2) -> (f2.isEditable() ? 1 : 0) - (f1.isEditable() ? 1 : 0)).toArray(YMLField[]::new);
    }

    @Override
    public String[] getSearchTags(YMLField f) {
        return new String[]{f.getPath()};
    }

    @Override
    public GuiEntry getEntry(YMLField ymlField) {
        SimpleItem item;
        if(ymlField.isBoolean()){
            item = new SimpleItem(XMaterial.TORCH)
                    .setDisplayName("&a" + L.FILE_MANAGER_YML_EDITOR_BOOLEAN_NAME)
                    .setLore(
                            "&7",
                            "&7" + L.FILE_MANAGER_YML_EDITOR_BOOLEAN_ACTION,
                            "&7",
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_SECTION,
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_PREVIEW
                    )
                    .addPlaceholder("{Section}", ymlField.getPath())
                    .addPlaceholder("{Preview}", Utils.parseEnabledBoolean(ymlField.asBoolean()));
            return new GuiEntry(item, a-> {
                ymlField.toggle();
                this.open();
            });
        }else if(ymlField.isString()){
            item = new SimpleItem(XMaterial.STRING)
                    .setDisplayName("&a" + L.FILE_MANAGER_YML_EDITOR_STRING_NAME)
                    .setLore(
                            "&7",
                            "&7" + L.FILE_MANAGER_YML_EDITOR_STRING_ACTION,
                            "&7",
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_SECTION,
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_PREVIEW
                    )
                    .addPlaceholder("{Section}", ymlField.getPath())
                    .addPlaceholder("{Preview}", ymlField.asString());
            return new GuiEntry(item, a-> new Dialog(a.player){
                @Override
                public String getTitle() {
                    return L.FILE_MANAGER_SET_STRING_TITLE.toString();
                }

                @Override
                public String getSubtitle() {
                    return L.FILE_MANAGER_SET_STRING_SUBTITLE.toString();
                }

                @Override
                public String getActionbar() {
                    return L.FILE_MANAGER_SET_STRING_ACTIONBAR.toString();
                }

                @Override
                public boolean onResult(String s) {
                    ymlField.set(s);
                    return true;
                }
            }.setRecall(p-> this.open()).addPlaceholder("{CurrentValue}", ymlField.asString()));
        }else if(ymlField.isNumber()){
            Number n = ymlField.asNumber();
            String amount = (n instanceof Double || n instanceof Float) ? "1.0" : "1";
            item = new SimpleItem(XMaterial.STONE)
                    .setDisplayName("&a" + L.FILE_MANAGER_YML_EDITOR_NUMBER_NAME)
                    .setLore(
                            "&7",
                            "&9" + Base.LEFT_CLICK + "&7 " + L.FILE_MANAGER_YML_EDITOR_NUMBER_LEFT,
                            "&9Q&7 " + L.FILE_MANAGER_YML_EDITOR_NUMBER_Q,
                            "&9" + Base.RIGHT_CLICK + "&7 " + L.FILE_MANAGER_YML_EDITOR_NUMBER_RIGHT,
                            "&7",
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_SECTION,
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_PREVIEW
                    )
                    .addPlaceholder("{Amount}", amount)
                    .addPlaceholder("{Preview}", n+"")
                    .addPlaceholder("{Section}", ymlField.getPath());
            return new GuiEntry(item, a-> {
                if(a.clickType == ClickType.LEFT_CLICK){
                    if(n instanceof Double){
                        ymlField.increase(1.0D);
                    }else if(n instanceof Float){
                        ymlField.increase(1.0F);
                    }else if(n instanceof Long){
                        ymlField.increase(1L);
                    }else{
                        ymlField.increase(1);
                    }
                }else if(a.clickType == ClickType.RIGHT_CLICK){
                    if(n instanceof Double){
                        ymlField.decrease(1.0D);
                    }else if(n instanceof Float){
                        ymlField.decrease(1.0F);
                    }else if(n instanceof Long){
                        ymlField.decrease(1L);
                    }else{
                        ymlField.decrease(1);
                    }
                }else if(a.clickType == ClickType.Q){
                    new Dialog(a.player){
                        @Override
                        public String getTitle() {
                            return L.FILE_MANAGER_SET_NUMBER_TITLE.toString();
                        }

                        @Override
                        public String getSubtitle() {
                            return L.FILE_MANAGER_SET_NUMBER_SUBTITLE.toString();
                        }

                        @Override
                        public String getActionbar() {
                            return L.FILE_MANAGER_SET_NUMBER_ACTIONBAR.toString();
                        }

                        @Override
                        public boolean onResult(String s) {
                            try{
                                Number toSet;
                                if(n instanceof Double){
                                    toSet = Double.parseDouble(s);
                                }else if(n instanceof Float){
                                    toSet = Float.parseFloat(s);
                                }else if(n instanceof Long){
                                    toSet = Long.parseLong(s);
                                }else{
                                    toSet = Integer.parseInt(s);
                                }

                                ymlField.set(toSet);
                                return true;
                            }catch (NumberFormatException e){
                                this.getSuperUtils().sendMessage(a.player, L.FILE_MANAGER_INVALID_NUMBER.toString());
                                return false;
                            }
                        }
                    }.addPlaceholder("{CurrentValue}", n+"").setRecall(p-> this.open());
                }
            });
        }else if(ymlField.isStringList()){
            List<String> list = ymlField.asStringList();
            if(!this.currentLine.containsKey(ymlField.getPath())){
                this.currentLine.put(ymlField.getPath(), 0);
            }

            int currentLine = this.currentLine.get(ymlField.getPath());
            item = new SimpleItem(XMaterial.BOOK)
                    .setDisplayName("&a" + L.FILE_MANAGER_YML_EDITOR_STRING_LIST_NAME)
                    .setLore(
                            "&7",
                            "&9" + Base.LEFT_CLICK + "&7 " + L.FILE_MANAGER_YML_EDITOR_STRING_LIST_LEFT,
                            "&9Q&7 " + L.FILE_MANAGER_YML_EDITOR_STRING_LIST_Q,
                            "&9" + Base.RIGHT_CLICK + "&7 " + L.FILE_MANAGER_YML_EDITOR_STRING_LIST_RIGHT,
                            "&7",
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_SECTION,
                            "&7" + L.FILE_MANAGER_YML_EDITOR_ITEM_PREVIEW
                    )
                    .addPlaceholder("{Preview}", "")
                    .addPlaceholder("{Section}", ymlField.getPath());
            for(int i = 0; i < list.size(); ++i){
                String s =  list.get(i);
                if(i == currentLine){
                    item.addLoreLine("&e> &r" + s);
                }else{
                    item.addLoreLine("&7- &r" + s);
                }
            }

            return new GuiEntry(item, a-> {
                if(a.clickType == ClickType.LEFT_CLICK){
                    new Dialog(a.player){
                        @Override
                        public String getTitle() {
                            return L.FILE_MANAGER_ADD_TO_LIST_TITLE.toString();
                        }

                        @Override
                        public String getSubtitle() {
                            return L.FILE_MANAGER_ADD_TO_LIST_SUBTITLE.toString();
                        }

                        @Override
                        public String getActionbar() {
                            return L.FILE_MANAGER_ADD_TO_LIST_ACTIONBAR.toString();
                        }

                        @Override
                        public boolean onResult(String s) {
                            list.add(s);
                            ymlField.set(list);
                            return true;
                        }
                    }.setRecall(p-> {
                        this.currentLine.put(ymlField.getPath(), 0);
                        this.open();
                    });
                }else if(a.clickType == ClickType.Q){
                    int next = currentLine == list.size()-1 ? 0 : currentLine+1;
                    this.currentLine.put(ymlField.getPath(), next);
                    this.open();
                }else if(a.clickType == ClickType.RIGHT_CLICK){
                    this.getSuperUtils().sendMessage(a.player, L.FILE_MANAGER_REMOVE_FROM_LIST.toString());
                    new Dialog(a.player){
                        @Override
                        public String getTitle() {
                            return L.FILE_MANAGER_ADD_TO_LIST_TITLE.toString();
                        }

                        @Override
                        public String getSubtitle() {
                            return L.FILE_MANAGER_ADD_TO_LIST_SUBTITLE.toString();
                        }

                        @Override
                        public String getActionbar() {
                            return L.FILE_MANAGER_ADD_TO_LIST_ACTIONBAR.toString();
                        }

                        @Override
                        public boolean onResult(String s) {
                            if(s.equals("yml-editor:line-remove")){
                                ymlField.removeInStringList(currentLine);
                            }else{
                                ymlField.setInStringList(currentLine, s);
                            }
                            return true;
                        }
                    }.setRecall(p-> {
                        this.currentLine.put(ymlField.getPath(), 0);
                        this.open();
                    });
                }
            });
        }else{
            item = new SimpleItem(XMaterial.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("&a" + L.FILE_MANAGER_YML_EDITOR_UNKNOWN_NAME)
                    .setLore(
                            "&7",
                            "&7" + L.FILE_MANAGER_YML_EDITOR_UNKNOWN_LORE
                    );
            return new GuiEntry(item);
        }
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.FILE_MANAGER_YML_EDITOR_TITLE.options().placeholder("{FileName}", this.file.getName()).get());
    }

}
