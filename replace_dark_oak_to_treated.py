import os
import os.path

for path_postfix in [
    'assets/greenhouses/blockstates',
    'assets/greenhouses/models/block',
    'assets/greenhouses/models/item',
    'data/greenhouses/loot_table/blocks',
    'data/greenhouses/recipe'
]:
    for filename in os.listdir('src/main/resources/' + path_postfix):
        if not filename.startswith('dark_oak'): continue

        filename = f'src/main/resources/{path_postfix}/{filename}'

        with open(filename, 'r', encoding='utf-8') as r:
            filetext = r.read()

            with open(filename, 'w', encoding='utf-8') as w:
                w.write(
                    filetext.replace('minecraft:dark_oak', 'greenhouses:treated')
                    .replace('minecraft:block/dark_oak', 'greenhouses:block/treated')
                )

        os.rename(filename, filename.replace('dark_oak', 'treated'))
