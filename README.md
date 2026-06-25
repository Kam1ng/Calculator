# 多功能计算器 (Multi-Function Calculator)

一个基于 Java Swing 开发的多功能计算器程序。

## 功能特性

### 基本运算
- 加、减、乘、除
- 小数点支持
- 幂运算 (^)
- 百分比计算

### 科学计算
- 三角函数：sin, cos, tan
- 反三角函数：asin, acos
- 对数函数：
  - `lg(x)`：常用对数（以10为底）
  - `ln(x)`：自然对数（以e为底）
  - `log(base, x)`：指定底数的对数
- 平方根：sqrt
- 数学常数：π, e

### 表达式解析
- 支持完整的表达式解析
- 运算符优先级处理
- 括号支持

### 历史记录
- 自动保存计算历史
- 按日期分类显示
- 支持清空历史

### 用户界面
- 支持键盘输入
- 光标移动编辑
- 科学模式切换

## 技术栈

- Java 8+
- Swing (图形界面)
- 栈式表达式解析

## 编译运行

```bash
# 编译
javac -encoding UTF-8 Calculator.java

# 运行
java Calculator
```

## 使用说明

### 基本操作
- 点击按钮或使用键盘输入数字和运算符
- 按 Enter 或点击 = 计算结果

### 对数函数用法
| 函数 | 格式 | 示例 | 结果 |
|------|------|------|------|
| lg | lg(x) | lg(100) | 2.0 |
| ln | ln(x) | ln(e) | 1.0 |
| log | log(base, x) | log(2,8) | 3.0 |

**注意**：log函数需要使用逗号分隔底数和参数，科学面板提供逗号按钮。

### 键盘快捷键
| 按键 | 功能 |
|------|------|
| 0-9, . | 输入数字 |
| +, -, *, /, ^ | 运算符 |
| (, ) | 括号 |
| Enter | 计算 |
| Esc / Ctrl+C | 清空 |
| Backspace | 删除 |
| Ctrl+P | 输入 π |
| Ctrl+E | 输入 e |

### 菜单功能
- **File → View History**: 查看历史记录
- **File → Clear History**: 清空历史记录
- **View → Scientific Mode**: 切换科学模式
- **Help → About**: 关于程序

## 项目结构

```
├── Calculator.java    # 主程序
├── README.md          # 项目说明
├── CHANGELOG.md       # 更新日志
└── .gitignore         # Git 忽略配置
```

## 作者

Kam1ng From GDUT

## 许可证

MIT License
