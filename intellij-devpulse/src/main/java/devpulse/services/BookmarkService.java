package devpulse.services;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import devpulse.models.Bookmark;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
    name = "DevPulseBookmarks",
    storages = @Storage("DevPulseBookmarks.xml")
)
public class BookmarkService implements PersistentStateComponent<BookmarkService> {

    public List<Bookmark> bookmarks = new ArrayList<>();

    public static BookmarkService getInstance(@NotNull Project project) {
        return project.getService(BookmarkService.class);
    }

    @Override
    public @Nullable BookmarkService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BookmarkService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void addBookmark(Bookmark bookmark) {
        if (!bookmarks.contains(bookmark)) {
            bookmarks.add(bookmark);
        }
    }

    public void removeBookmark(String filePath, int line) {
        bookmarks.removeIf(b -> b.filePath.equals(filePath) && b.line == line);
    }

    public List<Bookmark> getBookmarks() {
        return new ArrayList<>(bookmarks);
    }

    public int getCount() {
        return bookmarks.size();
    }
}
