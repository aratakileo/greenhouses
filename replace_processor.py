import os.path

replaceable_str = input('Replaceable value: ')
new_str = input('New value: ')

for path_postfix in [
    'assets/greenhouses/blockstates',
    'assets/greenhouses/models/block',
    'assets/greenhouses/models/item',
    'data/greenhouses/loot_table/blocks',
    'data/greenhouses/recipe'
]:
    for filename in os.listdir('src/main/resources/' + path_postfix):
        if not filename.startswith(replaceable_str): continue

        filename = f'src/main/resources/{path_postfix}/{filename}'

        with open(filename, 'r', encoding='utf-8') as r:
            filetext = r.read()

            with open(filename, 'w', encoding='utf-8') as w:
                w.write(
                    filetext.replace(f'minecraft:{replaceable_str}', f'greenhouses:{new_str}')
                    .replace(f'minecraft:block/{replaceable_str}', f'greenhouses:block/{new_str}')
                )

        os.rename(filename, filename.replace(replaceable_str, new_str))

print('Finished!')
