#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
vue_path = root / 'src' / 'views' / 'patient' / 'his-table.vue'
mock_path = root / 'mock-100-data.txt'

mock_block = mock_path.read_text(encoding='utf-8').strip()
content = vue_path.read_text(encoding='utf-8')

pattern = re.compile(r'const\s+mockData\s*=\s*\[[\s\S]*?\];', re.M)

new_content, n = pattern.subn(mock_block.split('\n',1)[1].strip() if mock_block.startswith('//') else mock_block, content, count=1)

if n == 0:
    # Try matching with leading spaces and comment
    pattern2 = re.compile(r'//\s*Mock[\s\S]*?\n\s*const\s+mockData\s*=\s*\[[\s\S]*?\];', re.M)
    new_content, n = pattern2.subn(mock_block, content, count=1)

if n == 0:
    print('No mockData block found to replace.')
else:
    vue_path.write_text(new_content, encoding='utf-8')
    print(f'Replaced mockData block ({n} occurrence).')



