import json
import os
import uuid

def force_wrap_outliner_elements_bbmodel():
    """
    Processes all .bbmodel files in the current working directory.
    It wraps the elements from the 'outliner' list in a new dictionary
    structure and updates the 'outliner' key, regardless of its current state.
    """
    current_working_directory = os.getcwd()
    print(f"Searching for .bbmodel files in: {current_working_directory}")

    found_files = False
    for filename in os.listdir(current_working_directory):
        if filename.endswith(".bbmodel"):
            found_files = True
            file_path = os.path.join(current_working_directory, filename)
            print(f"Processing file: {file_path}")

            try:
                with open(file_path, 'r+', encoding='utf-8') as f:
                    data = json.load(f)

                    if "outliner" in data and isinstance(data["outliner"], list):
                        original_outliner_elements = data["outliner"]

                        # Create the new wrapping structure
                        wrapped_structure = {
                            "name": "_all_model",
                            "origin": [0, 0, 0],
                            "color": 0,
                            "uuid": str(uuid.uuid4()),  # Generate a random dashed UUID
                            "export": True,
                            "isOpen": True,
                            "locked": False,
                            "visibility": True,
                            "autouv": 0,
                            "children": original_outliner_elements
                        }

                        # Replace the original outliner list with the new wrapped structure
                        data["outliner"] = [wrapped_structure]

                        # Go to the beginning of the file to overwrite it
                        f.seek(0)
                        json.dump(data, f, indent=4, ensure_ascii=False)
                        f.truncate() # Truncate any remaining old content if the new content is smaller
                        print(f"Successfully updated '{filename}'.")
                    else:
                        print(f"Skipping '{filename}': 'outliner' key not found or is not a list.")

            except json.JSONDecodeError:
                print(f"Error: Could not decode JSON from '{filename}'. Skipping.")
            except Exception as e:
                print(f"An unexpected error occurred while processing '{filename}': {e}")

    if not found_files:
        print("No .bbmodel files found in the current directory.")


# --- How to use the script ---
if __name__ == "__main__":
    print("\n--- Starting .bbmodel file conversion ---")
    force_wrap_outliner_elements_bbmodel()
    print("\n--- Conversion process finished. ---")