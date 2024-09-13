# 设置源目录和目标目录
$source_dir = "D:\youdaonote"
$output_dir = "D:\docx"

# 设置 PowerShell 为 UTF-8 编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# 创建目标目录（如果不存在）
if (!(Test-Path -Path $output_dir)) {
    Write-Host "Creating output directory: $output_dir"
    New-Item -ItemType Directory -Path $output_dir
} else {
    Write-Host "Output directory exists: $output_dir"
}

# 遍历所有 Markdown 文件并使用 Pandoc 转换为 Word 文档
Get-ChildItem -Path $source_dir -Recurse -Filter *.md | ForEach-Object {
    $md_file = $_.FullName
    $relative_path = $md_file.Substring($source_dir.Length)
    $output_subdir = Join-Path $output_dir (Split-Path -Parent $relative_path)

    # 创建输出目录（如果不存在）
    if (!(Test-Path -Path $output_subdir)) {
        Write-Host "Creating output directory: $output_subdir"
        New-Item -ItemType Directory -Path $output_subdir
    }

    $output_file = Join-Path $output_subdir ($_.BaseName + ".docx")

    # 设置资源路径，包含当前文件的目录和 ./images 子目录
    $resource_path = "$($md_file | Split-Path -Parent);$($md_file | Split-Path -Parent)\images"

    # 调用 Pandoc 进行转换，使用动态的 resource path
    Write-Host "Converting $md_file to $output_file with resource path: $resource_path"
    pandoc $md_file --resource-path="$resource_path" --extract-media="$output_subdir\media" --highlight-style=kate --standalone --verbose -o $output_file

    # 检查转换是否成功
    if (Test-Path -Path $output_file) {
        Write-Host "Conversion successful: $output_file"
    } else {
        Write-Host "Conversion failed for file: $md_file"
    }
}
