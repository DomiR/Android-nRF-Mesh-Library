package no.nordicsemi.android.meshprovisioner.data;



import androidx.annotation.RestrictTo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import no.nordicsemi.android.meshprovisioner.Scene;

@SuppressWarnings("unused")
@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface SceneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final Scene scene);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final List<Scene> scenes);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(final Scene scene);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(List<Scene> scenes);

    @Delete
    void delete(final Scene scene);

    @Query("DELETE FROM scene")
    void deleteAll();
}
