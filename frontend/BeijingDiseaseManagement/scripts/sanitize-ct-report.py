#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
vue_path = root / 'src' / 'views' / 'patient' / 'his-table.vue'
content = vue_path.read_text(encoding='utf-8')

# Replace all occurrences of "胸部CT报告": "..." (multiline) with backtick string
pattern = re.compile(r'("胸部CT报告"\s*:\s*")(.*?)("\s*,)', re.S)

def repl(m):
    inner = m.group(2)
    # Escape backticks if any (unlikely)
    inner = inner.replace('`', '\\`')
    return f'"胸部CT报告": `{inner}`,'

new_content, n = pattern.subn(repl, content)

if n:
    vue_path.write_text(new_content, encoding='utf-8')
    print(f'Replaced {n} multi-line CT report strings with template literals.')
else:
    print('No multi-line CT report strings found.')



