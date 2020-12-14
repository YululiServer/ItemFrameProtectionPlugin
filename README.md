# ItemFrameProtectionPlugin
なにかとお騒がせな額縁保護プラグインもついにVer 4.0

## テーブル構造
- 仮
### ItemFrameProtect (額縁保護テーブル)
- ProtectID (UUID, 主キー)
- ProtectWorldUUID (UUID)
- ProtectUser(UUID)

### DropItemList (ドロップアイテム対策用)
- DropItemID (UUID, 主キー)
- WorldUUID (UUID)

## ToDo
### システム関係
- [ ] 額縁保護データのテーブル設計
- [ ] ドロップアイテム記録のテーブル設計
- [ ] 額縁に入れたときにアイテムを減らすかどうか決めれるようにする

### 無限増殖対策
- [ ] 額縁から取り出し
- [ ] 額縁破壊
- [ ] ドロップアイテム
- [ ] 他人受け渡し
- [ ] チェスト等経由(チェストトロッコ等)
