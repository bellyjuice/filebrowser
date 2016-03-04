package com.lazyframework.standalone.filebrowser;

import java.io.File;
import java.io.FilenameFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Message;
import android.text.TextUtils;

import com.lazyframework.standalone.filebrowser.support.FileBrowserUtils;

/**
 * A runnable to browse file, use it in a thread or thread pool
 */
@SuppressWarnings("unused")
public class FileBrowserRunnable extends UISafeRunnable {

    public interface OnBrowseListener {
        void onFileDataLoaded(List<FileData> list);

        void onExtraFileDataLoaded(Map<String, ExtraFileData> map);

        void onBrowsingFolder(String folder);
    }

    public static class FileData {
        public String filePath;
        public String name;
        public boolean isDirectory;
        public long fileLength;
        @SuppressWarnings("unused")
        public ExtraFileData extra;
    }

    public static class ExtraFileData {
        public int fileCountInFolder;

        @SuppressWarnings("unused")
        public void assign(ExtraFileData efd) {
            this.fileCountInFolder = efd.fileCountInFolder;
        }
    }

    private static final int WHAT_FILE_DATA_LOADED = 1;
    private static final int WHAT_EXTRA_FILE_DAT_LOADED = 2;
    private static final int WHAT_BROWSING_FOLDER = 3;

    private static class FileDataList {
        public List<FileData> list;

        public FileDataList(List<FileData> list) {
            this.list = list;
        }
    }

    private static class ExtraFileDataMap {
        public Map<String, ExtraFileData> map;

        public ExtraFileDataMap(Map<String, ExtraFileData> map) {
            this.map = map;
        }
    }

    public static class LocaleComparator implements Comparator<File> {
        private Collator collator = Collator.getInstance();

        public int compare(File o1, File o2) {
            boolean o1IsDirectory = o1.isDirectory();
            boolean o2IsDirectory = o2.isDirectory();
            String o1Name = o1.getName();
            String o2Name = o2.getName();
            if ((o1IsDirectory && o2IsDirectory) || (!o1IsDirectory && !o2IsDirectory)) {
                if (collator.compare(o1Name, o2Name) < 0) {
                    return -1;
                } else if (collator.compare(o1Name, o2Name) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            } else if (o1IsDirectory) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private static class BrowseCmd {
        String path;
        int level;

        private BrowseCmd(String path, int level) {
            this.path = path;
            this.level = level;
        }
    }

    private final static int MAX_EXTRA_FILE_DATA_TO_LOAD_IN_A_TURN = 5;
    private FileBrowseParam param;

    private static class FileBrowseParam {
        private FileBrowseParam() {
            // no outside creation
        }

        private OnBrowseListener listener;
        private boolean loadFileCountInFolder;
        private List<BrowseCmd> browseCmdList = new ArrayList<>();
        private boolean showFolder = true;
        private boolean showFile = true;
        private Map<String, Object> suffixFilterMap;
        private List<String> mimeTypeFilterList;
        private List<String> filenameFilterList;
        private boolean filterByFileLength;
        private long minFileLength;
        private long maxFileLength = Long.MAX_VALUE;
        private Map<String, Object> excludeFolderMap;
    }

    public static class FileBrowseParamBuilder {
        private FileBrowseParam param = new FileBrowseParam();

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder setOnBrowseListener(OnBrowseListener listener) {
            param.listener = listener;
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder setLoadFileCountInFolder(boolean set) {
            param.loadFileCountInFolder = set;
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder addBrowseFolder(String folderPath) {
            return addBrowseFolder(folderPath, 0);
        }

        public FileBrowseParamBuilder addBrowseFolder(String folderPath, int browseLevel) {
            if (!TextUtils.isEmpty(folderPath) && browseLevel >= 0) {
                for (BrowseCmd bd : param.browseCmdList) {
                    if (folderPath.equals(bd.path)) {
                        bd.level = bd.level > browseLevel ? bd.level : browseLevel;
                        // found and return
                        return this;
                    }
                }
                param.browseCmdList.add(new BrowseCmd(folderPath, browseLevel));
            }
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder addSuffixFilter(String suffix) {
            if (!TextUtils.isEmpty(suffix)) {
                if (param.suffixFilterMap == null) {
                    param.suffixFilterMap = new HashMap<>();
                }
                param.suffixFilterMap.put(suffix, new Object());
            }
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder addMimeTypeFilter(String mimeType) {
            if (!TextUtils.isEmpty(mimeType)) {
                if (param.mimeTypeFilterList == null) {
                    param.mimeTypeFilterList = new ArrayList<>();
                }
                param.mimeTypeFilterList.add(mimeType);
            }
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder addFilenameFilter(String filename) {
            if (!TextUtils.isEmpty(filename)) {
                if (param.filenameFilterList == null) {
                    param.filenameFilterList = new ArrayList<>();
                }
                param.filenameFilterList.add(filename);
            }
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder setShowFolder(boolean set) {
            param.showFolder = set;
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder setShowFile(boolean set) {
            param.showFile = set;
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder setMinFileLength(long length) {
            if (length > 0) {
                param.minFileLength = length;
                param.filterByFileLength = true;
            }
            if (param.minFileLength > param.maxFileLength) {
                param.filterByFileLength = false;
            }
            return this;
        }

        @SuppressWarnings({"unused"})
        public FileBrowseParamBuilder setMaxFileLength(long length) {
            if (length > 0) {
                param.maxFileLength = length;
                param.filterByFileLength = true;
            }
            if (param.minFileLength > param.maxFileLength) {
                param.filterByFileLength = false;
            }
            return this;
        }

        public FileBrowseParamBuilder excludeFolder(String folder) {
            if (!TextUtils.isEmpty(folder)) {
                if (param.excludeFolderMap == null) {
                    param.excludeFolderMap = new HashMap<>();
                }
                param.excludeFolderMap.put(folder, new Object());
            }
            return this;
        }

        public FileBrowseParam create() {
            if (param.showFolder) {
                // showFolder is not compatible with the filters below
                if (param.filterByFileLength || param.mimeTypeFilterList != null || param.suffixFilterMap != null) {
                    param.showFolder = false;
                }
            }
            return param;
        }
    }

    @SuppressWarnings({"unused"})
    public FileBrowserRunnable(FileBrowseParamBuilder builder) {
        this.param = builder.create();
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }
        BrowseCmd data;
        while ((data = getNextBrowseCmd()) != null) {
            loadFiles(data.path, data.level);
        }
    }

    private BrowseCmd getNextBrowseCmd() {
        if (hasNextBrowseCmd()) {
            return param.browseCmdList.remove(param.browseCmdList.size() - 1);
        }
        return null;
    }

    private boolean hasNextBrowseCmd() {
        return param.browseCmdList.size() > 0;
    }

    private void pushNextLevelBrowseCmd(List<File> files, int level) {
        for (int i = files.size() - 1; i >= 0; i--) {
            File f = files.get(i);
            if (!f.isDirectory()) {
                continue;
            }
            BrowseCmd bd = new BrowseCmd(f.getPath(), level);
            param.browseCmdList.add(bd);
        }
    }

    private void loadFiles(String folderPath, int level) {
        if (isFolderExcluded(folderPath)) {
            if (!hasNextBrowseCmd()) {
                sendMessageToUI(WHAT_FILE_DATA_LOADED, new FileDataList(new ArrayList<FileData>()));
            }
            return;
        }
        File folder = new File(folderPath);
        File[] fa = null;
        if (folder.exists() && folder.isDirectory() && folder.canRead()) {
            sendMessageToUI(WHAT_BROWSING_FOLDER, folderPath);
            fa = folder.listFiles(sDefaultFilenameFilter);
        }
        if (isCancelled()) {
            return;
        }
        List<File> fl = new ArrayList<>();
        if (fa != null) {
            Collections.addAll(fl, fa);
        }
        Collections.sort(fl, new LocaleComparator());
        List<File> matchedFolders = null;
        List<FileData> fileDataList = new ArrayList<>();
        for (File f : fl) {
            if (!isInFilter(f)) {
                continue;
            }
            FileData fd = getFileData(f);
            if (param.loadFileCountInFolder && fd.isDirectory) {
                if (matchedFolders == null) {
                    matchedFolders = new ArrayList<>();
                }
                matchedFolders.add(f);
            }
            fileDataList.add(fd);
        }
        if (isCancelled()) {
            return;
        }
        if (level > 0) {
            pushNextLevelBrowseCmd(fl, level - 1);
        }
        if (hasNextBrowseCmd()) {
            if (fileDataList.size() > 0) {
                sendMessageToUI(WHAT_FILE_DATA_LOADED, new FileDataList(fileDataList));
            }
        } else {
            sendMessageToUI(WHAT_FILE_DATA_LOADED, new FileDataList(fileDataList));
        }
        loadFileCountInFolder(matchedFolders);
    }

    private FileData getFileData(File f) {
        FileData fd = new FileData();
        fd.filePath = f.getPath();
        fd.name = f.getName();
        fd.isDirectory = f.isDirectory();
        if (!fd.isDirectory) {
            fd.fileLength = f.length();
        }
        return fd;
    }

    private boolean isFolderExcluded(String folder) {
        return param.excludeFolderMap != null && (param.excludeFolderMap.get(folder) != null);
    }

    private boolean isInFilter(File file) {
        boolean isDirectory = file.isDirectory();
        String filename = file.getName();
        if (isDirectory && param.showFolder) {
            boolean filenameMatch = true;
            if (param.filenameFilterList != null) {
                filenameMatch = false;
                if (!TextUtils.isEmpty(filename)) {
                    for (String f : param.filenameFilterList) {
                        if (filename.contains(f)) {
                            filenameMatch = true;
                        }
                    }
                }
            }
            if (filenameMatch) {
                return true;
            }
        } else if (!isDirectory && param.showFile) {
            boolean lengthMatch = false;
            if (param.filterByFileLength) {
                long length = file.length();
                if (param.minFileLength <= length && param.maxFileLength >= length) {
                    lengthMatch = true;
                }
            } else {
                lengthMatch = true;
            }
            boolean suffixMatch = true;
            if (param.suffixFilterMap != null) {
                suffixMatch = false;
                String suffix = FileBrowserUtils.getSuffix(filename);
                if (param.suffixFilterMap.get(suffix) != null) {
                    suffixMatch = true;
                }
            }
            boolean mimeTypeMatch = true;
            if (param.mimeTypeFilterList != null) {
                mimeTypeMatch = false;
                String mimeType = FileBrowserUtils.getMimeType(filename);
                if (!TextUtils.isEmpty(mimeType)) {
                    for (String mimeTypePrefix : param.mimeTypeFilterList) {
                        if (mimeType.startsWith(mimeTypePrefix)) {
                            mimeTypeMatch = true;
                        }
                    }
                }
            }
            boolean filenameMatch = true;
            if (param.filenameFilterList != null) {
                filenameMatch = false;
                if (!TextUtils.isEmpty(filename)) {
                    for (String f : param.filenameFilterList) {
                        if (filename.contains(f)) {
                            filenameMatch = true;
                        }
                    }
                }
            }
            if (lengthMatch && suffixMatch && mimeTypeMatch && filenameMatch) {
                return true;
            }
        }
        return false;
    }

    private void loadFileCountInFolder(List<File> files) {
        if (files == null || files.size() <= 0) {
            return;
        }
        Map<String, ExtraFileData> map = new HashMap<>();
        for (File f : files) {
            ExtraFileData efd = null;
            if (f.isDirectory()) {
                String[] internalFiles = f.list(sDefaultFilenameFilter);
                if (internalFiles != null) {
                    efd = new ExtraFileData();
                    efd.fileCountInFolder = internalFiles.length;
                }
            }
            if (efd != null) {
                map.put(f.getPath(), efd);
            }
            if (map.size() >= MAX_EXTRA_FILE_DATA_TO_LOAD_IN_A_TURN) {
                if (isCancelled()) {
                    return;
                }
                sendMessageToUI(WHAT_EXTRA_FILE_DAT_LOADED, new ExtraFileDataMap(map));
                map = new HashMap<>();
            }
        }
        if (map.size() > 0) {
            if (isCancelled()) {
                return;
            }
            sendMessageToUI(WHAT_EXTRA_FILE_DAT_LOADED, new ExtraFileDataMap(map));
        }
    }

    private static FilenameFilter sDefaultFilenameFilter = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String filename) {
            File f = new File(dir, filename);
            return f.canRead() && !f.isHidden();
        }
    };

    @Override
    public void handleMessageInUI(Message msg) {
        switch (msg.what) {
            case WHAT_FILE_DATA_LOADED:
                if (msg.obj instanceof FileDataList && param.listener != null) {
                    FileDataList fdl = (FileDataList) msg.obj;
                    param.listener.onFileDataLoaded(fdl.list);
                }
                break;
            case WHAT_EXTRA_FILE_DAT_LOADED:
                if (msg.obj instanceof ExtraFileDataMap && param.listener != null) {
                    ExtraFileDataMap efdm = (ExtraFileDataMap) msg.obj;
                    param.listener.onExtraFileDataLoaded(efdm.map);
                }
                break;
            case WHAT_BROWSING_FOLDER:
                if (msg.obj instanceof String && param.listener != null) {
                    String folder = (String) msg.obj;
                    param.listener.onBrowsingFolder(folder);
                }
                break;
            default:
                break;
        }
    }
}
