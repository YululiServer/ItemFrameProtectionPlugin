package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ItemFrameData {
    private final Plugin plugin;

    private List<FrameData> frameList = new ArrayList<>();
    private List<DropData> dropList = new ArrayList<>();


    public ItemFrameData(Plugin plugin){
        this.plugin = plugin;
    }

    public List<FrameData> getItemFrameList(){
        return frameList;
    }

    public FrameData getItemFrame(UUID createUser, UUID itemFrame){
        for (int i = 0; i < frameList.size(); i++){
            if (frameList.get(i).getCreateUser().equals(createUser) && frameList.get(i).getItemFrame().equals(itemFrame)){
                return frameList.get(i);
            }
        }

        return null;
    }

    public FrameData getItemFrame(UUID itemFrame){
        for (int i = 0; i < frameList.size(); i++){
            if (frameList.get(i).getItemFrame().equals(itemFrame)){
                return frameList.get(i);
            }
        }

        return null;
    }

    public void addFrameList(FrameData data){
        frameList.add(data);
    }

    public void delFrameList(FrameData data){
        new BukkitRunnable() {
            @Override
            public void run() {
                int i = 0;
                while (true){
                    if (frameList.get(i).getCreateUser().equals(data.getCreateUser()) && frameList.get(i).getItemFrame().equals(data.getItemFrame())){
                        frameList.remove(i);
                        break;
                    }
                    i++;
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);
    }

    public List<DropData> getDropList(){
        return dropList;
    }

    public DropData getDropDataByItem(UUID dropItem){
        for (int i = 0; i < dropList.size(); i++){
            if (dropList.get(i).getItemUUID().equals(dropItem)){
                return dropList.get(i);
            }
        }

        return null;
    }

    public List<DropData> getDropDataByUser(UUID dropUser){

        List<DropData> data = new ArrayList<>();
        for (int i = 0; i < dropList.size(); i++){
            if (dropList.get(i).getDropUser().equals(dropUser)){
                data.add(dropList.get(i));
            }
        }

        return data;
    }

    public void addDropList(DropData data){
        dropList.add(data);
    }

    public void delDropList(DropData data){
        new BukkitRunnable() {
            @Override
            public void run() {
                int i = 0;
                while (true){
                    if (dropList.get(i).getDropUser().equals(data.getDropUser()) && dropList.get(i).getItemUUID().equals(data.getItemUUID())){
                        dropList.remove(i);
                        break;
                    }
                    i++;
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);
    }
}
